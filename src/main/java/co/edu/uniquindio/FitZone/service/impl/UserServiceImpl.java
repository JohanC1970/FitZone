package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.CreateUserRequest;
import co.edu.uniquindio.FitZone.dto.response.UserResponse;
import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import co.edu.uniquindio.FitZone.service.interfaces.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserResponse registerUser(CreateUserRequest request) {


        return null;
    }

    @Override
    public UserResponse uptadeUser(Long idUser, CreateUserRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long idUser) {

    }

    @Override
    public UserResponse getUserById(Long idUser) {

        return null;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return List.of();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserResponse getUserByDocumentNumber(String documentNumber) {
        return null;
    }
}
