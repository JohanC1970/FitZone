package co.edu.uniquindio.FitZone.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // Añadir esta importación
import org.springframework.web.cors.CorsConfigurationSource; // Añadir esta importación
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Añadir esta importación

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad para la aplicación FitZone.
 * Define las reglas de seguridad, incluyendo autenticación y autorización.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * @param http el objeto HttpSecurity para configurar la seguridad web
     * @return la cadena de filtros de seguridad configurada
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        // ✅ AÑADIR los nuevos endpoints del flujo de login aquí
                        .requestMatchers("/auth/login-2fa").permitAll()
                        .requestMatchers("/auth/verify-otp").permitAll()
                        .requestMatchers("/auth/resend-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/public/register").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura la fuente de configuración de CORS.
     *
     * @return la fuente de configuración de CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite las peticiones desde el origen de tu front-end de Angular
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Permite los métodos HTTP que vas a usar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Permite los encabezados (headers) comunes y el encabezado de autorización
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Permite el envío de cookies y credenciales
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica la configuración a todos los endpoints
        return source;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
