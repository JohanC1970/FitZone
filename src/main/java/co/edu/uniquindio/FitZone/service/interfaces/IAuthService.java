package co.edu.uniquindio.FitZone.service.interfaces;

import co.edu.uniquindio.FitZone.dto.request.LoginRequest;
import co.edu.uniquindio.FitZone.dto.request.ResetPasswordRequest;
import co.edu.uniquindio.FitZone.dto.request.VerifyOtpRequest;
import co.edu.uniquindio.FitZone.dto.response.OtpResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * Interfaz que define los metodos del servicio de autenticacion
 */
public interface IAuthService {

    /**
     * Método para verificar credenciales y generar un OTP
     * @param request DTO que contiene las credenciales para iniciar sesión
     * @return
     */
    OtpResponse loginAndGenerateOtp(LoginRequest request);

    /**
     * Método para verificar que el OTP sea correcto y terminar el proceso de inicio de sesión
     * @param request DTO que contiene el email y el OTP
     * @return Token para iniciar sesión
     */
    String verifyOtp(VerifyOtpRequest request);

    /**
     * Metodo para solicitar el reseteo de contraseña
     * @param email del usuario que solicita el reseteo
     */
    void requestPasswordReset(String email) throws IOException;

    /**
     * Metodo para resetear la contrasena
     * @param request contiene el token y la nueva contrasena
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Reenvia el OTP
     * @param email
     * @return
     */
    OtpResponse resendOtp(String email);
}
