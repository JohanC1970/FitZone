package co.edu.uniquindio.FitZone.dto.response.mercadopago;

public record PaymentPreferenceResponse(
        String preferenceId,
        String redirectUrl
) {
}
