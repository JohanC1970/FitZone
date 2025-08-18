package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Clase que implementa UserDetailsService para cargar los detalles del usuario
 * a partir de su correo electrónico.
 * Esta clase es utilizada por Spring Security para autenticar usuarios.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontro ningun usuario con el correo electrónico: " + email));

        //Por ahora, solo usamos el email y la contraseña
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),new ArrayList<>());
    }


}
