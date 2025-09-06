package co.edu.uniquindio.FitZone.dto.request.mercadopago;

import co.edu.uniquindio.FitZone.model.enums.MembershipTypeName;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentPreferenceRequest(
        @NotNull(message = "El ID del usuario no puede ser nulo")
        Long userId,

        @NotNull(message = "El ID del tipo de membresía no puede ser nulo")
        Long membershipTypeId,

        @NotNull(message = "El nombre del tipo de membresía no puede ser nulo")
        MembershipTypeName membershipTypeName,

        @NotNull(message = "El precio de la membresía no puede ser nulo")
        BigDecimal price
) {
}
