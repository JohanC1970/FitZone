package co.edu.uniquindio.FitZone.integration.payment;

import co.edu.uniquindio.FitZone.dto.request.mercadopago.PaymentPreferenceRequest;
import co.edu.uniquindio.FitZone.dto.response.mercadopago.PaymentPreferenceResponse;
import co.edu.uniquindio.FitZone.exception.LocationNotFoundException;
import co.edu.uniquindio.FitZone.exception.UserNotFoundException;
import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para manejar la integración con Mercado Pago.
 */
@Service
public class MercadoPagoService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final UserRepository userRepository;

    public MercadoPagoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Crea una preferencia de pago para una membresía.
     * @param request La solicitud con los detalles del pago.
     * @return Un objeto PaymentPreferenceResponse con el ID y la URL de redirección.
     */
    public PaymentPreferenceResponse createPaymentPreference(PaymentPreferenceRequest request) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            PreferenceClient client = new PreferenceClient();

            // ✅ Obtener el ID de la sede principal del usuario
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            if (user.getMainLocation() == null) {
                throw new LocationNotFoundException("El usuario no tiene una sede principal asignada.");
            }
            Long mainLocationId = user.getMainLocation().getIdLocation();

            List<PreferenceItemRequest> items = new ArrayList<>();
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(request.membershipTypeName().toString())
                    .description("Pago de membresía " + request.membershipTypeName().toString())
                    .quantity(1)
                    .unitPrice(request.price())
                    .build();
            items.add(itemRequest);

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:4200/payment-success")
                    .pending("http://localhost:4200/payment-pending")
                    .failure("http://localhost:4200/payment-failure")
                    .build();

            String notificationUrl = "http://tudominio.com/api/mercadopago/notifications";

            // ✅ Modificación: Combinar los IDs en un solo String para la externalReference
            String externalReference = String.format("%d_%d_%d",
                    request.userId(),
                    request.membershipTypeId(),
                    mainLocationId); // ✅ Usar el ID de la sede principal del usuario

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .externalReference(externalReference)
                    .backUrls(backUrls)
                    .notificationUrl(notificationUrl)
                    .build();

            Preference preference = client.create(preferenceRequest);
            logger.info("Preferencia de pago creada con éxito. ID: {}", preference.getId());

            return new PaymentPreferenceResponse(preference.getId(), preference.getInitPoint());

        } catch (MPApiException apiException) {
            logger.error("Error de la API de Mercado Pago: status={}, content={}", apiException.getApiResponse().getStatusCode(), apiException.getApiResponse().getContent());
            throw new RuntimeException("Error al crear la preferencia de pago con Mercado Pago", apiException);
        } catch (MPException exception) {
            logger.error("Error general de Mercado Pago: {}", exception.getMessage());
            throw new RuntimeException("Error inesperado al comunicarse con Mercado Pago", exception);
        }
    }
}
