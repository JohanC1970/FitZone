package co.edu.uniquindio.FitZone.controller;

import co.edu.uniquindio.FitZone.service.interfaces.IMembershipService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Map;

@RestController
@RequestMapping("/mercadopago")
public class MercadoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoController.class);
    private final IMembershipService membershipService;
    private static final Gson gson = new Gson();

    @Value("${mercadopago.webhook.secret}")
    private String webhookSecret;

    public MercadoPagoController(IMembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/notifications")
    public ResponseEntity<Void> handleWebhookNotification(
            @RequestBody String payload,
            @RequestHeader("x-request-id") String xRequestId,
            @RequestHeader("x-signature") String xSignature) {

        logger.info("Webhook de Mercado Pago recibido.");

        try {
            // --- LÓGICA DE VALIDACIÓN MANUAL Y CORRECTA ---
            if (webhookSecret == null || webhookSecret.isEmpty()) {
                logger.error("La clave secreta del webhook no está configurada en application.properties.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // 1. Separar el timestamp (ts) y el hash (v1) de la cabecera x-signature
            String ts = "";
            String hash = "";
            String[] signatureParts = xSignature.split(",");
            for (String part : signatureParts) {
                String[] keyValue = part.trim().split("=");
                if (keyValue.length == 2) {
                    if ("ts".equals(keyValue[0])) {
                        ts = keyValue[1];
                    } else if ("v1".equals(keyValue[0])) {
                        hash = keyValue[1];
                    }
                }
            }

            if (ts.isEmpty() || hash.isEmpty()) {
                logger.warn("Cabecera x-signature con formato inválido.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // 2. Extraer el 'data.id' del payload
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> notificationData = gson.fromJson(payload, type);
            Map<String, Object> data = (Map<String, Object>) notificationData.get("data");
            String dataId = (String) data.get("id");

            // 3. Crear el manifiesto para la firma
            String manifest = String.format("id:%s;request-id:%s;ts:%s;", dataId, xRequestId, ts);

            // 4. Calcular nuestra propia firma usando la clave secreta
            String ourSignature = new HmacUtils("HmacSHA256", webhookSecret).hmacHex(manifest);

            // 5. Comparar las firmas
            if (!ourSignature.equals(hash)) {
                logger.warn("¡FIRMA DE WEBHOOK INVÁLIDA! La notificación podría ser fraudulenta.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            logger.info("Firma del Webhook validada exitosamente.");
            // --- FIN DE LA VALIDACIÓN ---

            String topic = (String) notificationData.get("type");
            if ("payment".equalsIgnoreCase(topic)) {
                logger.info("Notificación de pago recibida para el ID: {}", dataId);
                membershipService.processMercadoPagoNotification(dataId);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error inesperado al procesar la notificación del webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}