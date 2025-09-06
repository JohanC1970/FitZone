package co.edu.uniquindio.FitZone.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
        @NotBlank(message = "El email no puede estar vació")
        @Email(message = "El email debe ser valido")
        String email,

        @NotBlank(message = "El OTP no puede estar vació")
        String otp
) {
}
