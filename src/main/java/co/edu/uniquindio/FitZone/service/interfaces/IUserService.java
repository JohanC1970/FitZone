package co.edu.uniquindio.FitZone.service.interfaces;

import co.edu.uniquindio.FitZone.dto.request.UserRequest;
<<<<<<< HEAD
import co.edu.uniquindio.FitZone.dto.request.UserUpdateRequest;
import co.edu.uniquindio.FitZone.dto.request.LoginRequest;
=======
>>>>>>> bc96e7c (Se documentaron algunas clases faltantes y se agregó la clase service para MembershipType)
import co.edu.uniquindio.FitZone.dto.response.UserResponse;

import java.util.List;

/**
 * Define los contratos del servicio de usuario.
 * Este servicio maneja la lógica de negocio relacionada con los usuarios.
 * Aquí se pueden definir métodos para registrar, autenticar, actualizar y eliminar usuarios,
 * así como la verificación de 2 pasos (OTP).
 */
public interface IUserService {

<<<<<<< HEAD
    // ----------------- USUARIO -----------------

    UserResponse registerUser(UserRequest request);

    UserResponse publicRegisterUser(UserRequest request);

    UserResponse updateUser(Long idUser, UserUpdateRequest request);
=======
    /**
     * Registra un nuevo usuario en el sistema.
     * Este método recibe un objeto CreateUserRequest que contiene la información necesaria para crear un usuario
     * @param request objeto que contiene los datos del usuario a registrar
     * @return UserResonse objeto que representa la respuesta del registro del usuario
     */
    UserResponse registerUser(UserRequest request);

    /**
     * Actualiza la información de un usuario existente.
     * Este método recibe un ID de usuario y un objeto CreateUserRequest que contiene los datos actualizados del usuario.
     * @param idUser ID del usuario a actualizar
     * @param request objeto que contiene los datos actualizados del usuario
     * @return UserResponse objeto que contiene la información actualizada del usuario
     */
    UserResponse updateUser(Long idUser, UserRequest request);
>>>>>>> bc96e7c (Se documentaron algunas clases faltantes y se agregó la clase service para MembershipType)

    void deleteUser(Long idUser);

    UserResponse getUserById(Long idUser);

    List<UserResponse> getAllUsers();

    UserResponse getUserByEmail(String email);

    UserResponse getUserByDocumentNumber(String documentNumber);

    // ----------------- AUTENTICACIÓN 2FA -----------------

    /**
     * Valida las credenciales iniciales del usuario (email + password).
     * @param request LoginRequest con email y contraseña
     * @return true si las credenciales son válidas, false en caso contrario
     */
    boolean validateCredentials(LoginRequest request);

    /**
     * Genera un OTP (One Time Password) para el usuario dado su email.
     * @param email Email del usuario
     * @return OTP generado
     */
    String generateOTP(String email);

    /**
     * Envía el OTP al correo electrónico del usuario.
     * @param email Email del usuario
     * @param otp OTP generado
     */
    void sendOTPEmail(String email, String otp);

    /**
     * Valida el OTP ingresado por el usuario.
     * @param email Email del usuario
     * @param otp OTP ingresado
     * @return true si el OTP es válido y no ha expirado, false en caso contrario
     */
    boolean validateOTP(String email, String otp);

    /**
     * Genera el token JWT después de que el OTP ha sido validado exitosamente.
     * @param email Email del usuario
     * @return Token JWT
     */
    String loginAfterOTP(String email);
}
