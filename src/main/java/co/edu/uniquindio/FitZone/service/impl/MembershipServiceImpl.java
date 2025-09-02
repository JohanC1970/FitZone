package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.CreateMembershipRequest;
import co.edu.uniquindio.FitZone.dto.request.SuspendMembershipRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipResponse;
import co.edu.uniquindio.FitZone.exception.LocationNotFoundException;
import co.edu.uniquindio.FitZone.exception.MembershipTypeNotFoundException;
import co.edu.uniquindio.FitZone.exception.ResourceAlreadyExistsException;
import co.edu.uniquindio.FitZone.exception.UserNotFoundException;
import co.edu.uniquindio.FitZone.integration.payment.StripeService;
import co.edu.uniquindio.FitZone.model.entity.Location;
import co.edu.uniquindio.FitZone.model.entity.Membership;
import co.edu.uniquindio.FitZone.model.entity.MembershipType;
import co.edu.uniquindio.FitZone.model.entity.User;
import co.edu.uniquindio.FitZone.model.enums.MembershipStatus;
import co.edu.uniquindio.FitZone.repository.LocationRepository;
import co.edu.uniquindio.FitZone.repository.MembershipRepository;
import co.edu.uniquindio.FitZone.repository.MembershipTypeRepository;
import co.edu.uniquindio.FitZone.repository.UserRepository;
import co.edu.uniquindio.FitZone.service.interfaces.IMembershipService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class MembershipServiceImpl implements IMembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final MembershipTypeRepository membershipTypeRepository;
    private final LocationRepository locationRepository;
    private final StripeService stripeService;

    public MembershipServiceImpl(MembershipRepository membershipRepository, UserRepository userRepository, MembershipTypeRepository membershipTypeRepository, LocationRepository locationRepository, StripeService stripeService) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.membershipTypeRepository = membershipTypeRepository;
        this.locationRepository = locationRepository;
        this.stripeService = stripeService;
    }


    @Override
    public MembershipResponse createMembership(CreateMembershipRequest request) {

        //Validar que los recursos existan
        User user = userRepository.findById(request.userId())
                .orElseThrow( () -> new UserNotFoundException("El usuario no existe"));

        if(user.getMembership() != null){

            if(user.getMembership().getStatus() == MembershipStatus.ACTIVE){
                throw new ResourceAlreadyExistsException("El usuario ya tiene una membresía activa");
            }

            if(user.getMembership().getStatus() == MembershipStatus.SUSPENDED ){
                throw new ResourceAlreadyExistsException("El usuario ya tiene una membresía registrada, " +
                        "pero esta se encuentra suspendida por la siguiente razón: " + user.getMembership().getSuspensionReason());

            }
        }

        MembershipType type = membershipTypeRepository.findById(request.MembershipTypeId())
                .orElseThrow(()-> new MembershipTypeNotFoundException("Tipo de membresía no encontrada en el sistema"));

        Location location = locationRepository.findById(request.mainLocationId())
                .orElseThrow(()-> new LocationNotFoundException("Sede principal no encontrada"));

        try{

            //Verificar el pago con Stripe usando el ID de la intención de pago
            PaymentIntent paymentIntent = stripeService.getPaymentIntent(request.paymentIntentId());

            if(!"succeeded".equals(paymentIntent.getStatus())){
                throw new RuntimeException("El pago no se ha completado correctamente");
            }

            // Creamos y guardamos la membresía en la base de datos
            Membership newMembership = new Membership();
            newMembership.setUser(user);
            newMembership.setType(type);
            newMembership.setLocation(location);
            newMembership.setPrice(type.getMonthlyPrice());
            newMembership.setStartDate(LocalDate.now());
            newMembership.setEndDate(LocalDate.now().plusMonths(1));
            newMembership.setStatus(MembershipStatus.ACTIVE);

            Membership savedMembership = membershipRepository.save(newMembership);

            //Actualizamos la referencia de la membresía en el usuario
            user.setMembership(savedMembership);
            user.setMainLocation(location);
            userRepository.save(user);

            //Retornamos la respuesta al cliente
            return new MembershipResponse(
                    savedMembership.getIdMembership(),
                    savedMembership.getUser().getIdUser(),
                    savedMembership.getType().getName(),
                    savedMembership.getLocation().getIdLocation(),
                    savedMembership.getStartDate(),
                    savedMembership.getEndDate(),
                    savedMembership.getStatus()

            );

        } catch (StripeException e) {
            throw new RuntimeException("Error al verificar el pago con Stripe: " + e.getMessage());
        }

    }

    @Override
    public MembershipResponse getMembershipByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("El usuario no existe"));

        if (user.getMembership() == null) {
            throw new MembershipTypeNotFoundException("El usuario no tiene una membresía activa");
        }

        Membership membership = user.getMembership();
        return new MembershipResponse(
                membership.getIdMembership(),
                membership.getUser().getIdUser(),
                membership.getType().getName(),
                membership.getLocation().getIdLocation(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getStatus()
        );
    }

    @Override
    public MembershipResponse getMembershipByDocumentNumber(String documentNumber) {

        User user = userRepository.findByPersonalInformation_DocumentNumber(documentNumber)
                .orElseThrow(() -> new UserNotFoundException("El usuario no existe"));

        if (user.getMembership() == null) {
            throw new MembershipTypeNotFoundException("El usuario no tiene una membresía activa");
        }

        Membership membership = user.getMembership();
        return new MembershipResponse(
                membership.getIdMembership(),
                membership.getUser().getIdUser(),
                membership.getType().getName(),
                membership.getLocation().getIdLocation(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getStatus()
        );
    }

    @Override
    public MembershipResponse suspendMembership(SuspendMembershipRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("El usuario no existe"));
        Membership membership = user.getMembership();

        if (membership == null) {
            throw new MembershipTypeNotFoundException("El usuario no tiene una membresía");
        }

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new ResourceAlreadyExistsException("La membresía ya está suspendida o cancelada");
        }

        membership.setStatus(MembershipStatus.SUSPENDED);
        membership.setSuspensionReason(request.suspensionReason());
        membership.setSuspensionStart(LocalDate.now());
        membership.setSuspensionEnd(request.suspensionEnd());

        Membership updatedMembership = membershipRepository.save(membership);

        return new MembershipResponse(
                updatedMembership.getIdMembership(),
                updatedMembership.getUser().getIdUser(),
                updatedMembership.getType().getName(),
                updatedMembership.getLocation().getIdLocation(),
                updatedMembership.getStartDate(),
                updatedMembership.getEndDate(),
                updatedMembership.getStatus()
        );

    }

    @Override
    public MembershipResponse reactivateMembership(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("El usuario no existe"));
        Membership membership = user.getMembership();

        if (membership == null || membership.getStatus() != MembershipStatus.SUSPENDED) {
            throw new RuntimeException("La membresía no puede ser reactivada ya que esta no se encuentra suspendida");
        }

        // Calcula la duración real de la suspensión y extiende la fecha de finalización.
        long suspensionDays = ChronoUnit.DAYS.between(membership.getSuspensionStart(), LocalDate.now());
        membership.setEndDate(membership.getEndDate().plusDays(suspensionDays));

        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setSuspensionReason(null);
        membership.setSuspensionStart(null);
        membership.setSuspensionEnd(null);

        Membership updatedMembership = membershipRepository.save(membership);

        return new MembershipResponse(
                updatedMembership.getIdMembership(),
                updatedMembership.getUser().getIdUser(),
                updatedMembership.getType().getName(),
                updatedMembership.getLocation().getIdLocation(),
                updatedMembership.getStartDate(),
                updatedMembership.getEndDate(),
                updatedMembership.getStatus()
        );
    }

    @Override
    public void cancelMembership(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("El usuario no existe"));
        Membership membership = user.getMembership();

        if (membership == null) {
            throw new MembershipTypeNotFoundException("El usuario no tiene una membresía");
        }

        membership.setStatus(MembershipStatus.CANCELLED);
        membershipRepository.save(membership);
    }


}
