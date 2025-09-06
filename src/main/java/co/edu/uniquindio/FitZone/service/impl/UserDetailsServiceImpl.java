package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Clase que implementa UserDetailsService para cargar los detalles del usuario
 * a partir de su correo electrónico.
 * Esta clase es utilizada por Spring Security para autenticar usuarios.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Método generado por la implementación de UserDetailsService
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Cargando detalles del usuario para autenticación - Email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado para autenticación - Email: {}", email);
                    return new UsernameNotFoundException("No se encontro ningun usuario con el correo electrónico: " + email);
                });

        logger.debug("Usuario encontrado para autenticación - ID: {}, Nombre: {}, Email: {}", 
            user.getIdUser(), user.getPersonalInformation().getFirstName(), email);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole());

        logger.debug("Creando UserDetails para Spring Security - Email: {}", email);
        return new CustomUserDetails(
                user.getIdUser(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
