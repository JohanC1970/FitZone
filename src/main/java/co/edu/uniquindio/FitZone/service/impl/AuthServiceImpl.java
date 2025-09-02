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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Implementación del servicio de autenticación.
 * Proporciona métodos para iniciar sesión, solicitar restablecimiento de contraseña y restablecer la contraseña.
 */
@Service
public class AuthServiceImpl implements IAuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

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
        logger.info("Iniciando proceso de login para el usuario: {}", request.email());
        
        try{
            logger.debug("Autenticando credenciales del usuario: {}", request.email());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            logger.debug("Autenticación exitosa para el usuario: {}", request.email());
        }catch (BadCredentialsException e){
            logger.warn("Credenciales incorrectas para el usuario: {}", request.email());
            throw new BadCredentialsException("Credenciales incorrectas", e);
        }

        logger.debug("Cargando detalles del usuario: {}", request.email());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtUtil.generateToken(userDetails);
        logger.info("Login exitoso para el usuario: {}. Token JWT generado", request.email());
        return token;
    }


    @Override
    public void requestPasswordReset(String email) throws IOException {
        logger.info("Solicitando restablecimiento de contraseña para el usuario: {}", email);
        
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Usuario no encontrado para restablecimiento de contraseña: {}", email);
            return new UserNotFoundException("Usuario no encontrado");
        });

        String token = RandomStringUtils.randomNumeric(6);
        logger.debug("Token de restablecimiento generado para el usuario {}: {}", email, token);
        
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        logger.debug("Token de restablecimiento guardado en la base de datos para el usuario: {}", email);

        //Crear el contexto para el email que se enviara
        Context context = new Context();

        //Agregamos los datos que necesita la plantilla
        context.setVariable("userName", user.getPersonalInformation().getFirstName());
        context.setVariable("verificationCode", token);
        context.setVariable("expiryDate", user.getPasswordResetTokenExpiryDate());
        context.setVariable("gymEmail", "fitzoneuq@gmail.com");

        String subject = "Recuperación de contraseña - FitZone";

        logger.debug("Enviando email de restablecimiento de contraseña al usuario: {}", email);
        emailService.sendTemplatedEmail(user.getEmail(), subject, "password-reset", context);
        logger.info("Email de restablecimiento de contraseña enviado exitosamente al usuario: {}", email);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        logger.info("Iniciando proceso de restablecimiento de contraseña con token: {}", request.token());
        
        User user = userRepository.findByPasswordResetToken(request.token())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado para el token de restablecimiento: {}", request.token());
                    return new UserNotFoundException("Usuario no encontrado");
                });

        logger.debug("Usuario encontrado para restablecimiento: {}", user.getEmail());

        if(user.getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())){
            logger.warn("Token de restablecimiento expirado para el usuario: {}", user.getEmail());
            throw new RuntimeException("El token ha expirado");
        }

        logger.debug("Token válido, procediendo a actualizar contraseña para el usuario: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiryDate(null);
        userRepository.save(user);
        logger.info("Contraseña restablecida exitosamente para el usuario: {}", user.getEmail());
    }


}
