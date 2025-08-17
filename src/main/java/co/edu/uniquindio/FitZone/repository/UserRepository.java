package co.edu.uniquindio.FitZone.repository;

import co.edu.uniquindio.FitZone.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Interfaz para operaciones CRUD de la entidad User.
 * Extiende JpaRepository para proporcionar métodos de acceso a datos.
 */
public interface UserRepository extends JpaRepository<User, Long> {


}
