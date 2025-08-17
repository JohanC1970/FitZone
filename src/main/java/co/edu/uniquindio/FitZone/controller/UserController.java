package co.edu.uniquindio.FitZone.controller;


import co.edu.uniquindio.FitZone.dto.request.CreateUserRequest;
import co.edu.uniquindio.FitZone.dto.response.UserResponse;
import co.edu.uniquindio.FitZone.service.interfaces.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public UserResponse registerUser(@RequestBody CreateUserRequest request){
        return userService.registerUser(request);
    }


}
