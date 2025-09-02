package co.edu.uniquindio.FitZone.repository;

import co.edu.uniquindio.FitZone.dto.response.MembershipResponse;
import co.edu.uniquindio.FitZone.model.entity.Membership;
import co.edu.uniquindio.FitZone.model.enums.MembershipStatus;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface MembershipRepository extends CrudRepository<Membership, Long> {


    /**
     * Busca membresías que estén suspendidas y cuya fecha de fin de suspensión ya haya pasado.
     * @param status Estado de la membresía.
     * @param suspensionEnd Fecha límite para la suspensión.
     * @return Una lista de membresías que deben ser reactivadas.
     */
    List<Membership> findByStatusAndSuspensionEndIsBefore(MembershipStatus status, LocalDate suspensionEnd);

    /**
     * Busca membresías por su estado y fecha de finalización.
     * @param status El estado de la membresía.
     * @param endDate La fecha exacta de finalización de la membresía.
     * @return Una lista de membresías que cumplen con los criterios.
     */
    List<Membership> findByStatusAndEndDate(MembershipStatus status, LocalDate endDate);

    List<Membership> findByStatusAndEndDateIsBefore(MembershipStatus status, LocalDate suspensionEnd);


}
