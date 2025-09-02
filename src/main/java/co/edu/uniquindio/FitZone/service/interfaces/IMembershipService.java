package co.edu.uniquindio.FitZone.service.interfaces;

import co.edu.uniquindio.FitZone.dto.request.CreateMembershipRequest;
import co.edu.uniquindio.FitZone.dto.request.SuspendMembershipRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipResponse;

public interface IMembershipService {

    MembershipResponse createMembership(CreateMembershipRequest request);

    MembershipResponse getMembershipByUserId(Long userId);

    MembershipResponse getMembershipByDocumentNumber(String documentNumber);

    MembershipResponse suspendMembership(SuspendMembershipRequest request);

    MembershipResponse reactivateMembership(Long userId);

    void cancelMembership(Long userId);

}
