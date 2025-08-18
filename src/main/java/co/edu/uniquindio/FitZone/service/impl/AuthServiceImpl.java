package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.LoginRequest;
import co.edu.uniquindio.FitZone.dto.request.ResetPasswordRequest;
import co.edu.uniquindio.FitZone.exception.UserNotFoundException;
import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import co.edu.uniquindio.FitZone.service.interfaces.IAuthService;
import co.edu.uniquindio.FitZone.util.EmailService;
import co.edu.uniquindio.FitZone.util.JwtUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;

public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    @Override
    public String login(LoginRequest request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Credenciales incorrectas", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        return jwtUtil.generateToken(userDetails);
    }


    @Override
    public void requestPasswordReset(String email) throws IOException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = RandomStringUtils.randomNumeric(6);
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        //Crear el contexto para el email que se enviara
        Context context = new Context();

        //Agregamos los datos que necesita la plantilla
        context.setVariable("userName", user.getPersonalInformation().getFirstName());
        context.setVariable("verificationCode", token);
        context.setVariable("expiryDate", user.getPasswordResetTokenExpiryDate());
        context.setVariable("gymEmail", "fitzoneuq@gmail.com");

        String subject = "Recuperación de contraseña - FitZone";

        emailService.sendTemplatedEmail(user.getEmail(), subject, "password-reset", context);

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.token())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if(user.getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("El token ha expirado");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiryDate(null);
        userRepository.save(user);
    }


}
