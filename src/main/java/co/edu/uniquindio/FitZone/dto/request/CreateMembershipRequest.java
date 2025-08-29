package co.edu.uniquindio.FitZone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMembershipRequest(

        @NotNull(message = "El ID del usuario no puede ser nulo")
        Long userId,

        @NotNull(message = "El ID del tipo de membresía no puede ser nulo")
        Long MembershipTypeId,

        @NotNull(message = "El ID de la sede principal no puede ser nulo")
        Long mainLocationId,

        @NotBlank(message = "El ID de la intención de pago no puede estar vacío")
        String paymentIntentId
) {
}
