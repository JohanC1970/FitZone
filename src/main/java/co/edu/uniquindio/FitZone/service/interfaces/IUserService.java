package co.edu.uniquindio.FitZone.service.interfaces;

import co.edu.uniquindio.FitZone.dto.request.CreateUserRequest;
import co.edu.uniquindio.FitZone.dto.response.UserResponse;

import java.util.List;

/**
 * Define los contratos del servicio de usuario.
 * Este servicio maneja la lógica de negocio relacionada con los usuarios.
 * Aquí se pueden definir métodos para registrar, autenticar, actualizar y eliminar usuarios.
 */
public interface IUserService {

    /**
     * Registra un nuevo usuario en el sistema.
     * Este método recibe un objeto CreateUserRequest que contiene la información necesaria para crear un usuario
     * @param request objeto que contiene los datos del usuario a registrar
     * @return UserResonse objeto que representa la respuesta del registro del usuario
     */
    UserResponse registerUser(CreateUserRequest request);

    /**
     * Actualiza la información de un usuario existente.
     * Este método recibe un ID de usuario y un objeto CreateUserRequest que contiene los datos actualizados del usuario.
     * @param idUser ID del usuario a actualizar
     * @param request objeto que contiene los datos actualizados del usuario
     * @return UserResponse objeto que contiene la información actualizada del usuario
     */
    UserResponse updateUser(Long idUser, CreateUserRequest request);

    /**
     * Elimina un usuario del sistema.
     * Este método recibe el ID del usuario a eliminar.
     * @param idUser ID del usuario a eliminar
     */
    void deleteUser(Long idUser);

    /**
     * Obtiene un usuario por su ID.
     * Este método recibe el ID del usuario y devuelve un objeto UserResponse que contiene la información del usuario.
     * @param idUser ID del usuario a buscar
     * @return UserResponse objeto que representa al usuario encontrado
     */
    UserResponse getUserById(Long idUser);

    /**
     * Obtiene una lista de todos los usuarios activos en el sistema.
     * Este método devuelve una lista de objetos UserResponse que representan a los usuarios activos.
     * @return List<UserResponse> lista de usuarios activos
     */
    List<UserResponse> getAllUsers();

    /**
     * Obtiene un usuario por su correo electrónico.
     * Este método recibe el correo electrónico del usuario y devuelve un objeto UserResponse que contiene la información del usuario.
     * @param email correo electrónico del usuario a buscar
     * @return UserResponse objeto que representa al usuario encontrado
     */
    UserResponse getUserByEmail(String email);

    /**
     * Obtiene un usuario por su número de documento.
     * Este método recibe el número de documento del usuario y devuelve un objeto UserResponse que contiene la información del usuario.
     * @param documentNumber número de documento del usuario a buscar
     * @return UserResponse objeto que representa al usuario encontrado
     */
    UserResponse getUserByDocumentNumber(String documentNumber);


}
