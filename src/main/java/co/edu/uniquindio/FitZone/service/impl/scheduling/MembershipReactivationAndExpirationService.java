package co.edu.uniquindio.FitZone.service.impl.scheduling;

import co.edu.uniquindio.FitZone.model.entity.Membership;
import co.edu.uniquindio.FitZone.model.enums.MembershipStatus;
import co.edu.uniquindio.FitZone.repository.MembershipRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MembershipReactivationAndExpirationService {

    private final MembershipRepository membershipRepository;
    public MembershipReactivationAndExpirationService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * Tarea programada que se ejecuta a medianoche para manejar la reactivación
     * de membresías suspendidas y la expiración de membresías activas.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Se ejecuta a las 00:00:00 todos los días
    public void manageMembershipsStatus() {
        LocalDate today = LocalDate.now();

        // Lógica para reactivar automáticamente las membresías suspendidas
        List<Membership> suspendedMemberships = membershipRepository.findByStatusAndSuspensionEndIsBefore(MembershipStatus.SUSPENDED, today);
        for (Membership membership : suspendedMemberships) {
            // Se calcula la duración de la suspensión con la fecha de fin estipulada
            long suspendedDays = ChronoUnit.DAYS.between(membership.getSuspensionStart(), membership.getSuspensionEnd());
            membership.setEndDate(membership.getEndDate().plusDays(suspendedDays));
            membership.setStatus(MembershipStatus.ACTIVE);
            membership.setSuspensionEnd(null);
            membership.setSuspensionReason(null);
            membershipRepository.save(membership);
        }

        // Lógica para cambiar el estado de las membresías que ya expiraron
        List<Membership> activeAndExpiredMemberships = membershipRepository.findByStatusAndEndDateIsBefore(MembershipStatus.ACTIVE, today);
        for (Membership membership : activeAndExpiredMemberships) {
            membership.setStatus(MembershipStatus.EXPIRED);
            membershipRepository.save(membership);
        }
    }


}
