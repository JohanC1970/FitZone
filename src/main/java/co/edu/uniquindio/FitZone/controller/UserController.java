package co.edu.uniquindio.FitZone.controller;


import co.edu.uniquindio.FitZone.dto.request.CreateUserRequest;
import co.edu.uniquindio.FitZone.dto.response.UserResponse;
import co.edu.uniquindio.FitZone.service.interfaces.IUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar las operaciones relacionadas con los usuarios.
 * Este controlador define los endpoints para registrar, actualizar, eliminar y obtener usuarios.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse registerUser(@RequestBody CreateUserRequest request){
        return userService.registerUser(request);
    }


    @PutMapping("/{idUser}")
    public UserResponse updateUser(@PathVariable Long idUser, @RequestBody CreateUserRequest request) {
        return userService.updateUser(idUser, request);
    }

    @DeleteMapping("/{idUser}")
    public void deleteUser(@PathVariable Long idUser) {
        userService.deleteUser(idUser);
    }

    @GetMapping("/{idUser}")
    public UserResponse getUserById(@PathVariable Long idUser) {
        return userService.getUserById(idUser);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/by-email")
    public UserResponse getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/by-document")
    public UserResponse getUserByDocumentNumber(@RequestParam String documentNumber) {
        return userService.getUserByDocumentNumber(documentNumber);
    }


}
