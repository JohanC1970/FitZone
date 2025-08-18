package co.edu.uniquindio.FitZone.service.interfaces;

import co.edu.uniquindio.FitZone.dto.request.LoginRequest;
import co.edu.uniquindio.FitZone.dto.request.ResetPasswordRequest;

import java.io.IOException;

/**
 * Interfaz que define los metodos del servicio de autenticacion
 */
public interface IAuthService {

    /**
     * Metodo para iniciar sesion
     * @param request contiene el email y la contrasena del usuario
     * @return un token JWT
     */
    String login(LoginRequest request);

    /**
     * Metodo para solicitar el reseteo de contrasena
     * @param email del usuario que solicita el reseteo
     */
    void requestPasswordReset(String email) throws IOException;

    /**
     * Metodo para resetear la contrasena
     * @param request contiene el token y la nueva contrasena
     */
    void resetPassword(ResetPasswordRequest request);


}
