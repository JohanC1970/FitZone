package co.edu.uniquindio.FitZone.controller;


import co.edu.uniquindio.FitZone.dto.request.LoginRequest;
import co.edu.uniquindio.FitZone.dto.request.ResetPasswordRequest;
import co.edu.uniquindio.FitZone.dto.request.VerifyOtpRequest;
import co.edu.uniquindio.FitZone.dto.response.OtpResponse;
import co.edu.uniquindio.FitZone.exception.UserNotFoundException;
import co.edu.uniquindio.FitZone.service.impl.UserDetailsServiceImpl;
import co.edu.uniquindio.FitZone.service.interfaces.IAuthService;
import co.edu.uniquindio.FitZone.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * Controlador para manejar las operaciones de autenticación y autorización.
 * Proporciona endpoints para iniciar sesión, solicitar restablecimiento de contraseña y restablecer la contraseña.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final IAuthService authService;


    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, IAuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/login-2fa") // Cambiar el nombre del endpoint para coincidir con el frontend
    public ResponseEntity<Object> loginAndGenerateOtp(@RequestBody LoginRequest request) {
        logger.info("POST /auth/login-2fa - Solicitud de login con 2FA para usuario: {}", request.email());

        try {
            OtpResponse response = authService.loginAndGenerateOtp(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al procesar la solicitud."));
        }
    }

    // Nuevo endpoint para verificar el OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<Object> verifyOtp(@RequestBody VerifyOtpRequest request) {
        logger.info("POST /auth/verify-otp - Verificación de OTP para usuario: {}", request.email());

        try {
            String token = authService.verifyOtp(request);
            return ResponseEntity.ok(Map.of("accessToken", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al procesar la solicitud."));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        logger.info("POST /auth/forgot-password - Solicitud de restablecimiento de contraseña para: {}", email);
        logger.debug("Solicitud de restablecimiento recibida - Email: {}", email);
        
        try {
            authService.requestPasswordReset(email);
            logger.info("Solicitud de restablecimiento de contraseña procesada exitosamente para: {}", email);
            return ResponseEntity.ok("Se ha enviado un email con las instrucciones para restablecer la contraseña");
        } catch (Exception e) {
            logger.error("Error al procesar solicitud de restablecimiento de contraseña para: {} - Error: {}", 
                email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        logger.info("POST /auth/reset-password - Restablecimiento de contraseña con token: {}", request.token());
        logger.debug("Solicitud de restablecimiento recibida - Token: {}", request.token());
        
        try {
            authService.resetPassword(request);
            logger.info("Contraseña restablecida exitosamente con token: {}", request.token());
            return ResponseEntity.ok("Contraseña restablecida exitosamente");
        } catch (Exception e) {
            logger.error("Error al restablecer contraseña con token: {} - Error: {}", 
                request.token(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error al restablecer la contraseña: " + e.getMessage());
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Object> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.info("POST /auth/resend-otp - Reenvío de OTP para usuario: {}", email);

        try {
            OtpResponse response = authService.resendOtp(email);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        } catch (Exception e) {
            logger.error("Error al reenviar OTP para: {} - Error: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al procesar la solicitud."));
        }
    }

}
