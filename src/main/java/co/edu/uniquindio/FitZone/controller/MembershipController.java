package co.edu.uniquindio.FitZone.controller;


import co.edu.uniquindio.FitZone.dto.request.CreateMembershipRequest;
import co.edu.uniquindio.FitZone.dto.request.PaymentIntentRequest;
import co.edu.uniquindio.FitZone.dto.request.SuspendMembershipRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipResponse;
import co.edu.uniquindio.FitZone.integration.payment.StripeService;
import co.edu.uniquindio.FitZone.service.interfaces.IMembershipService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("(/memberships")
public class MembershipController {

    private final IMembershipService membershipService;
    private final StripeService stripeService;

    public MembershipController(IMembershipService membershipService, StripeService stripeService) {
        this.membershipService = membershipService;
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<String>createPaymentIntent(@RequestBody PaymentIntentRequest request) throws StripeException {
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(request.amount().longValue(),
                request.currency(), request.description());

        return ResponseEntity.ok(paymentIntent.getClientSecret());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<MembershipResponse> createMembership(@RequestBody CreateMembershipRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.createMembership(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MembershipResponse> getMembershipByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(membershipService.getMembershipByUserId(userId));
    }

    @PatchMapping("/suspend")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<MembershipResponse> suspendMembership(@RequestBody SuspendMembershipRequest request){
        return ResponseEntity.ok(membershipService.suspendMembership(request));
    }

    @PatchMapping("/reactivate/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<MembershipResponse> reactivateMembership(@PathVariable Long userId){
        return ResponseEntity.ok(membershipService.reactivateMembership(userId));
    }

    @DeleteMapping("/cancel/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Void> cancelMembership(@PathVariable Long userId){
        membershipService.cancelMembership(userId);
        return ResponseEntity.noContent().build();
    }

}
