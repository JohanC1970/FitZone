package co.edu.uniquindio.FitZone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentIntentRequest(

        @NotNull(message = "El monto no puede ser nulo")
        BigDecimal amount,

        @NotBlank(message = "La divisa no puede estar vacía")
        String currency,

        String description
) {
}
