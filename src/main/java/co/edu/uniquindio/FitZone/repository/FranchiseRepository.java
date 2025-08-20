package co.edu.uniquindio.FitZone.repository;

import co.edu.uniquindio.FitZone.model.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    Optional<Franchise> findByName(String name);
}
