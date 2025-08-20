package co.edu.uniquindio.FitZone.repository;

import co.edu.uniquindio.FitZone.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {

    boolean existByName(String name);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByAddress(String address);

    Optional<Location> findByName(String name);

    List<Location> findByFranchiseId(Long franchiseId);

    List<Location> findByIsActiveTrue();

    Optional<Location> findByPhoneNumber(String phoneNumber);

    Optional<Location> findByAddress(String address);
}
