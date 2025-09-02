package co.edu.uniquindio.FitZone.service.impl.scheduling;

import co.edu.uniquindio.FitZone.model.entity.Membership;
import co.edu.uniquindio.FitZone.model.enums.MembershipStatus;
import co.edu.uniquindio.FitZone.repository.MembershipRepository;
import co.edu.uniquindio.FitZone.util.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MembershipNotificationService {

    private final MembershipRepository membershipRepository;
    private final EmailService emailService;

    public MembershipNotificationService(MembershipRepository membershipRepository, EmailService emailService) {
        this.membershipRepository = membershipRepository;
        this.emailService = emailService;

    }

    @Scheduled(cron = "0 0 6 * * *") // Se ejecuta todos los días a las 6:00 AM
    public void sendRenewalReminders() throws IOException {
        LocalDate today = LocalDate.now();

        // Membresías que expiran en 7 días
        LocalDate sevenDaysFromNow = today.plusDays(7);
        List<Membership> expiringInSevenDays = membershipRepository.findByStatusAndEndDate(MembershipStatus.ACTIVE, sevenDaysFromNow);
        sendNotification(expiringInSevenDays, 7);

        // Membresías que expiran en 1 día
        LocalDate oneDayFromNow = today.plusDays(1);
        List<Membership> expiringInOneDay = membershipRepository.findByStatusAndEndDate(MembershipStatus.ACTIVE, oneDayFromNow);
        sendNotification(expiringInOneDay, 1);
    }

    private void sendNotification(List<Membership> memberships, long daysRemaining) throws IOException, IOException {
        for (Membership membership : memberships) {
            Context context = new Context();
            context.setVariable("userName", membership.getUser().getPersonalInformation().getFirstName());
            context.setVariable("membershipType", membership.getType().getName());
            context.setVariable("expiryDate", membership.getEndDate());
            context.setVariable("daysRemaining", daysRemaining);
            context.setVariable("renewalLink", ""); // Falta remplazar con el enlace para renovar la membresía
            context.setVariable("gymPhone", "***");
            context.setVariable("gymEmail", "fitzoneuq@gmail.com");

            String subject = "Recordatorio de Renovación - FitZone";
            emailService.sendTemplatedEmail(membership.getUser().getEmail(), subject, "membership-renewal-reminder", context);
        }
    }

}
