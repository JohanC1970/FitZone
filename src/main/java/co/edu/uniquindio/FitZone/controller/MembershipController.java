package co.edu.uniquindio.FitZone.controller;


import co.edu.uniquindio.FitZone.dto.request.CreateMembershipRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipResponse;
import co.edu.uniquindio.FitZone.service.interfaces.IMembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("(/memberships")
public class MembershipController {

    private final IMembershipService membershipService;

    public MembershipController(IMembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/create")
    public ResponseEntity<MembershipResponse> createMembership(@RequestBody CreateMembershipRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.createMembership(request));
    }

}
