package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.UserRequest;
import co.edu.uniquindio.FitZone.dto.request.UserUpdateRequest;
import co.edu.uniquindio.FitZone.dto.response.UserResponse;
import co.edu.uniquindio.FitZone.exception.ResourceAlreadyExistsException;
import co.edu.uniquindio.FitZone.exception.UnauthorizedRegistrationException;
import co.edu.uniquindio.FitZone.exception.UserNotFoundException;
import co.edu.uniquindio.FitZone.model.entity.PersonalInformation;
import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.model.enums.UserRole;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import co.edu.uniquindio.FitZone.service.interfaces.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Clase SERVICE - Que implementa los métodos declarados en IUserService
 */
@Service
public class UserServiceImpl implements IUserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * -----------FALTA VINCULAR LA SEDE --------------
     * Método para registrar un usuario
     * @param request objeto que contiene los datos del usuario a registrar
     * @return UserResponse objeto de respuesta, contiene alguna información del usuario registrado
     */
    @Override
    public UserResponse registerUser(UserRequest request) {

        //Obtener el rol del usuario autenticado que está realizando la solicitud
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserRole registeringUserRole = UserRole.valueOf(authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rol de usuario autenticado no encontrado"))
                .getAuthority());

        //Un ADMIN puede registrar cualquier rol, incluido otro ADMIN
        //Los demás roles solo pueden registrar roles con un nivel de jerarquía inferior
        if (registeringUserRole != UserRole.ADMIN && registeringUserRole.getHierarchyLevel() <= request.role().getHierarchyLevel()) {
            throw new UnauthorizedRegistrationException("El usuario con rol " + registeringUserRole.name() + " no está autorizado para registrar un usuario con rol " + request.role().name());
        }

        //Validamos que el email y el número de documento no existan
        if(userRepository.existsByEmail(request.email())){
            throw  new ResourceAlreadyExistsException("El email ya se encuentra registrado.");
        }

        if(userRepository.existsByPersonalInformation_DocumentNumber(request.documentNumber())){
            throw new ResourceAlreadyExistsException("El número de documento ya se encuentra registrado.");
        }

        //Mapear el DTO a la entidad User
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        //Mapear la información del DTO al objeto embebido PersonalInformation
        PersonalInformation personalInformation = getPersonalInformation(request);

        user.setPersonalInformation(personalInformation);

        //Guardamos el usuario en la base de datos
        return getUserResponse(user);
    }

    @Override
    public UserResponse publicRegisterUser(UserRequest request) {

        if(userRepository.existsByEmail(request.email())){
            throw new ResourceAlreadyExistsException("El email ya se encuentra registrado.");
        }

        if(userRepository.existsByPersonalInformation_DocumentNumber(request.documentNumber())){
            throw new ResourceAlreadyExistsException("El número de documento ya se encuentra registrado.");
        }

        // Mapear el DTO a la entidad User
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.MEMBER); // Se asigna el rol de MEMBER de forma automática

        // Mapear la información del DTO al objeto embebido
        PersonalInformation personalInformation = getPersonalInformation(request);
        user.setPersonalInformation(personalInformation);

        // Guardar el usuario en la base de datos
        return getUserResponse(user);
    }

    /**
     * Extrae la información personal de un DTO de solicitud.
     * @param request Objeto que contiene los datos personales del usuario.
     * @return Un nuevo objeto PersonalInformation
     */
    private static PersonalInformation getPersonalInformation(UserRequest request) {
        PersonalInformation personalInformation = new PersonalInformation();
        personalInformation.setFirstName(request.firstName());
        personalInformation.setLastName(request.lastName());
        personalInformation.setDocumentType(request.documentType());
        personalInformation.setDocumentNumber(request.documentNumber());
        personalInformation.setBirthDate(request.birthDate());
        personalInformation.setMedicalConditions(request.medicalConditions());
        personalInformation.setEmergencyContactPhone(request.emergencyContactPhone());
        personalInformation.setPhoneNumber(request.phoneNumber());
        return personalInformation;
    }

    /**
     * Método para actualizar un usuario
     * @param idUser ID del usuario a actualizar
     * @param request objeto que contiene los datos actualizados del usuario
     * @return
     */
    @Override
    public UserResponse updateUser(Long idUser, UserUpdateRequest request) {
        // Buscar el usuario por su ID. Si no existe, lanzar una excepción.
        User existingUser = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el ID: " + idUser));

        // Actualizar solo los campos que no son nulos en la solicitud
        if(request.firstName() != null) {
            existingUser.getPersonalInformation().setFirstName(request.firstName());
        }
        if(request.lastName() != null) {
            existingUser.getPersonalInformation().setLastName(request.lastName());
        }
        if(request.email() != null && !existingUser.getEmail().equals(request.email())) {
            if(userRepository.existsByEmail(request.email())){
                throw new ResourceAlreadyExistsException("El nuevo email ya se encuentra registrado por otro usuario.");
            }
            existingUser.setEmail(request.email());
        }
        if(request.documentNumber() != null && !existingUser.getPersonalInformation().getDocumentNumber().equals(request.documentNumber())) {
            if(userRepository.existsByPersonalInformation_DocumentNumber(request.documentNumber())){
                throw new ResourceAlreadyExistsException("El nuevo número de documento ya se encuentra registrado por otro usuario.");
            }
            existingUser.getPersonalInformation().setDocumentNumber(request.documentNumber());
        }
        if(request.documentType() != null) {
            existingUser.getPersonalInformation().setDocumentType(request.documentType());
        }
        if(request.phoneNumber() != null) {
            existingUser.getPersonalInformation().setPhoneNumber(request.phoneNumber());
        }
        if(request.birthDate() != null) {
            existingUser.getPersonalInformation().setBirthDate(request.birthDate());
        }
        if(request.emergencyContactPhone() != null) {
            existingUser.getPersonalInformation().setEmergencyContactPhone(request.emergencyContactPhone());
        }
        if(request.medicalConditions() != null) {
            existingUser.getPersonalInformation().setMedicalConditions(request.medicalConditions());
        }

        return getUserResponse(existingUser);
    }

    private UserResponse getUserResponse(User existingUser) {
        User updatedUser = userRepository.save(existingUser);


        return new UserResponse(
                updatedUser.getIdUser(),
                updatedUser.getPersonalInformation().getFirstName(),
                updatedUser.getPersonalInformation().getLastName(),
                updatedUser.getEmail(),
                updatedUser.getPersonalInformation().getDocumentType(), // Agregado
                updatedUser.getPersonalInformation().getDocumentNumber(), // Agregado
                updatedUser.getPersonalInformation().getPhoneNumber(),
                updatedUser.getPersonalInformation().getBirthDate(), // Agregado
                updatedUser.getPersonalInformation().getEmergencyContactPhone(), // Agregado
                updatedUser.getPersonalInformation().getMedicalConditions(), // Agregado
                updatedUser.getRole(),
                updatedUser.getCreatedAt()
        );
    }

    /**
     * Actualiza la información personal del usuario existente con los datos del request
     * @param request Objeto que contiene los datos actualizados del usuario
     * @param existingUser Usuario existente en la base de datos
     * @return Objeto PersonalInformation actualizado
     */
    private static PersonalInformation getPersonalInformation(UserRequest request, User existingUser) {
        PersonalInformation personalInfo = existingUser.getPersonalInformation();
        personalInfo.setFirstName(request.firstName());
        personalInfo.setLastName(request.lastName());
        personalInfo.setDocumentType(request.documentType());
        personalInfo.setDocumentNumber(request.documentNumber());
        personalInfo.setBirthDate(request.birthDate());
        personalInfo.setPhoneNumber(request.phoneNumber());
        personalInfo.setMedicalConditions(request.medicalConditions());
        personalInfo.setEmergencyContactPhone(request.emergencyContactPhone());
        return personalInfo;
    }

    /**
     * Este método hace un borrado lógico del usuario (cambia el estado a false)
     * @param idUser ID del usuario a eliminar
     */
    @Override
    public void deleteUser(Long idUser) {

        User user = userRepository.findById(idUser)
                .orElseThrow(()-> new UserNotFoundException("Usuario no encontrado con el ID: " + idUser));

        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Método que obtiene a un usuario dado su id
     * @param idUser ID del usuario a buscar
     * @return UserResponse, objeto que contiene la información del usuario
     */
    @Override
    public UserResponse getUserById(Long idUser) {

        User user = userRepository.findById(idUser)
                .orElseThrow( () -> new UserNotFoundException("El id ingresado no existe"));

        return new UserResponse(
                user.getIdUser(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                user.getEmail(),
                user.getPersonalInformation().getDocumentType(), // Agregado
                user.getPersonalInformation().getDocumentNumber(), // Agregado
                user.getPersonalInformation().getPhoneNumber(),
                user.getPersonalInformation().getBirthDate(), // Agregado
                user.getPersonalInformation().getEmergencyContactPhone(), // Agregado
                user.getPersonalInformation().getMedicalConditions(), // Agregado
                user.getRole(),
                user.getCreatedAt()
        );
    }

    /**
     * Este método me devuelve todos los usuarios activos
     * @return
     */
    @Override
    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findByIsActiveTrue();

        return users.stream()
                .map(user -> new UserResponse(
                        user.getIdUser(),
                        user.getPersonalInformation().getFirstName(),
                        user.getPersonalInformation().getLastName(),
                        user.getEmail(),
                        user.getPersonalInformation().getDocumentType(), // Agregado
                        user.getPersonalInformation().getDocumentNumber(), // Agregado
                        user.getPersonalInformation().getPhoneNumber(),
                        user.getPersonalInformation().getBirthDate(), // Agregado
                        user.getPersonalInformation().getEmergencyContactPhone(), // Agregado
                        user.getPersonalInformation().getMedicalConditions(), // Agregado
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    /**
     * Método para obtener un usuario dado su correo electrónico
     * @param email correo electrónico del usuario a buscar
     * @return
     */
    @Override
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new UserNotFoundException("El email ingresado no existe"));

        return new UserResponse(
                user.getIdUser(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                user.getEmail(),
                user.getPersonalInformation().getDocumentType(), // Agregado
                user.getPersonalInformation().getDocumentNumber(), // Agregado
                user.getPersonalInformation().getPhoneNumber(),
                user.getPersonalInformation().getBirthDate(), // Agregado
                user.getPersonalInformation().getEmergencyContactPhone(), // Agregado
                user.getPersonalInformation().getMedicalConditions(), // Agregado
                user.getRole(),
                user.getCreatedAt()
        );
    }

    /**
     * Método para obtener un usuario dado su número de documento
     * @param documentNumber número de documento del usuario a buscar
     * @return
     */
    @Override
    public UserResponse getUserByDocumentNumber(String documentNumber) {
        User user = userRepository.findByPersonalInformation_DocumentNumber(documentNumber)
                .orElseThrow( () -> new UserNotFoundException("El número de documento ingresado no existe"));

        return new UserResponse(
                user.getIdUser(),
                user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),
                user.getEmail(),
                user.getPersonalInformation().getDocumentType(), // Agregado
                user.getPersonalInformation().getDocumentNumber(), // Agregado
                user.getPersonalInformation().getPhoneNumber(),
                user.getPersonalInformation().getBirthDate(), // Agregado
                user.getPersonalInformation().getEmergencyContactPhone(), // Agregado
                user.getPersonalInformation().getMedicalConditions(), // Agregado
                user.getRole(),
                user.getCreatedAt()
        );
    }



}
