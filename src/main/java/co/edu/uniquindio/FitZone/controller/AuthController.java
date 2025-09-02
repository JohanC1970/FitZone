package co.edu.uniquindio.FitZone.controller;


import co.edu.uniquindio.FitZone.dto.request.LoginRequest;
import co.edu.uniquindio.FitZone.dto.request.ResetPasswordRequest;
import co.edu.uniquindio.FitZone.service.impl.UserDetailsServiceImpl;
import co.edu.uniquindio.FitZone.service.interfaces.IAuthService;
import co.edu.uniquindio.FitZone.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        logger.info("POST /auth/login - Iniciando proceso de login para usuario: {}", request.email());
        logger.debug("Datos de login recibidos - Email: {}", request.email());
        
        try {
            String token = authService.login(request);
            logger.info("Login exitoso para usuario: {} - Token JWT generado", request.email());
            logger.debug("Login completado exitosamente - Email: {}", request.email());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.error("Error en el proceso de login para usuario: {} - Error: {}", 
                request.email(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación: " + e.getMessage());
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
}
