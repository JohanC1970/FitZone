package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.LoginRequest;
import co.edu.uniquindio.FitZone.dto.request.ResetPasswordRequest;
import co.edu.uniquindio.FitZone.dto.request.VerifyOtpRequest;
import co.edu.uniquindio.FitZone.dto.response.OtpResponse;
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
    public OtpResponse loginAndGenerateOtp(LoginRequest request) {
        logger.info("Iniciando el proceso de login para el usuario con email: {}", request.email());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            logger.debug("Autenticación exitosa para el usuario: {}", request.email());
        } catch (BadCredentialsException e) {
            logger.warn("Credenciales incorrectas para el usuario: {}", request.email());
            throw new BadCredentialsException("Credenciales incorrectas", e);
        }

        // Buscar el usuario y generar el OTP
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        String otp = RandomStringUtils.randomNumeric(6);
        user.setOtp(otp);
        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(15)); // Código válido por 15 minutos
        userRepository.save(user);

        // Crear el contexto y enviar el correo con el OTP
        Context context = new Context();
        context.setVariable("userName", user.getPersonalInformation().getFirstName());
        context.setVariable("verificationCode", otp);
        context.setVariable("expiryDate", user.getOtpExpiryDate());
        context.setVariable("gymEmail", "fitzoneuq@gmail.com");

        emailService.sendEmail(user.getEmail(), "Verificación de acceso - FitZone", "El codigo de verificacion: " + otp);

        return new OtpResponse(request.email(), "OTP_REQUIRED");
    }

    @Override
    public String verifyOtp(VerifyOtpRequest request) {
        logger.info("Verificando OTP para el usuario: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!request.otp().equals(user.getOtp())) {
            throw new RuntimeException("Código OTP incorrecto");
        }

        if (user.getOtpExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código OTP ha expirado");
        }

        // Limpiar OTP y generar el token JWT
        user.setOtp(null);
        user.setOtpExpiryDate(null);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtUtil.generateToken(userDetails);

        logger.info("Verificación de OTP exitosa para {}. Token JWT generado.", request.email());
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

    @Override
    public OtpResponse resendOtp(String email) {
        logger.info("Reenviando OTP para el usuario con email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Generar nuevo OTP
        String otp = RandomStringUtils.randomNumeric(6);
        user.setOtp(otp);
        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(15)); // Código válido por 15 minutos
        userRepository.save(user);

        // Crear el contexto y enviar el correo con el nuevo OTP
        Context context = new Context();
        context.setVariable("userName", user.getPersonalInformation().getFirstName());
        context.setVariable("verificationCode", otp);
        context.setVariable("expiryDate", user.getOtpExpiryDate());
        context.setVariable("gymEmail", "fitzoneuq@gmail.com");

        try {
            // CORREGIR: Usar template específico para OTP, no "password-reset"
            emailService.sendTemplatedEmail(user.getEmail(), "Código de verificación - FitZone", "password-reset", context);
        } catch (IOException e) {
            throw new RuntimeException("Error al enviar el email de OTP", e);
        }

        logger.info("OTP reenviado exitosamente para el usuario: {}", email);
        return new OtpResponse(email, "OTP_SENT");
    }


}
