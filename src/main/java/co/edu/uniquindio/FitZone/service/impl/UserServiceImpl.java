package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.CreateUserRequest;
import co.edu.uniquindio.FitZone.dto.response.UserResponse;
import co.edu.uniquindio.FitZone.exception.ResourceAlreadyExistsException;
import co.edu.uniquindio.FitZone.exception.UserNotFoundException;
import co.edu.uniquindio.FitZone.model.entity.PersonalInformation;
import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import co.edu.uniquindio.FitZone.service.interfaces.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public UserResponse registerUser(CreateUserRequest request) {

        //Validar que el email y el número de documento no existan
        if(userRepository.existsByEmail(request.email())){
            throw new ResourceAlreadyExistsException("El email ya se encuentre registrado.");
        }

        if(userRepository.existsByPersonalInformation_DocumentNumber(request.documentNumber())){
            throw new ResourceAlreadyExistsException("El número de documento ya se encuentra registrado.");
        }
        //Mapear el DTP a la entidad User
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        //Mapear la información del DTO al objeto embebido
        PersonalInformation personalInformation = getPersonalInformation(request);

        user.setPersonalInformation(personalInformation);


        //Guardar el usuario en la base de datos
        User savedUser = userRepository.save(user);

        //Mapear la entidad guardada a un DTO de respuesta
        return new UserResponse(savedUser.getIdUser(),
                savedUser.getPersonalInformation().getFirstName(),
                savedUser.getPersonalInformation().getLastName(),
                savedUser.getEmail(),
                savedUser.getPersonalInformation().getPhoneNumber(),
                savedUser.getRole(),
                savedUser.getCreatedAt()

        );
    }

    /**
     * Extrae la información personal
     * @param request
     * @return
     */
    private static PersonalInformation getPersonalInformation(CreateUserRequest request) {
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
    public UserResponse uptadeUser(Long idUser, CreateUserRequest request) {
        // 1. Buscar el usuario por su ID. Si no existe, lanzar una excepción.
        User existingUser = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el ID: " + idUser));

        // 2. Validar que el nuevo email no exista en otro usuario
        if (!existingUser.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("El nuevo email ya se encuentra registrado por otro usuario.");
        }

        // 3. Validar que el nuevo número de documento no exista en otro usuario
        if (!existingUser.getPersonalInformation().getDocumentNumber().equals(request.documentNumber()) && userRepository.existsByPersonalInformation_DocumentNumber(request.documentNumber())) {
            throw new ResourceAlreadyExistsException("El nuevo número de documento ya se encuentra registrado por otro usuario.");
        }

        // 4. Actualizar los campos de la entidad
        existingUser.setEmail(request.email());
        if (request.password() != null && !request.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }
        existingUser.setRole(request.role());

        // 5. Actualizar los objetos embebidos
        PersonalInformation personalInfo = existingUser.getPersonalInformation();
        personalInfo.setFirstName(request.firstName());
        personalInfo.setLastName(request.lastName());
        personalInfo.setDocumentType(request.documentType());
        personalInfo.setDocumentNumber(request.documentNumber());
        personalInfo.setBirthDate(request.birthDate());
        personalInfo.setPhoneNumber(request.phoneNumber());
        personalInfo.setMedicalConditions(request.medicalConditions());
        personalInfo.setEmergencyContactPhone(request.emergencyContactPhone());
        existingUser.setPersonalInformation(personalInfo);

        // 6. Guardar los cambios
        User updatedUser = userRepository.save(existingUser);

        // 7. Mapear y retornar la respuesta
        return new UserResponse(
                updatedUser.getIdUser(),
                updatedUser.getPersonalInformation().getFirstName(),
                updatedUser.getPersonalInformation().getLastName(),
                updatedUser.getEmail(),
                updatedUser.getPersonalInformation().getPhoneNumber(),
                updatedUser.getRole(),
                updatedUser.getCreatedAt()
        );
    }

    /**
     * Este método hace un borrado lógico del usuario (cambia el estado a false)
     * @param idUser ID del usuario a eliminar
     */
    @Override
    public void deleteUser(Long idUser) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent()){
            user.get().setActive(false);
            userRepository.save(user.get());
        }
        throw new ResourceAlreadyExistsException("El id ingresado no corresponde a ningun usuario registrado.");
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
                user.getPersonalInformation().getPhoneNumber(),
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
                        user.getPersonalInformation().getPhoneNumber(),
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
                user.getPersonalInformation().getPhoneNumber(),
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
                user.getPersonalInformation().getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }



}
