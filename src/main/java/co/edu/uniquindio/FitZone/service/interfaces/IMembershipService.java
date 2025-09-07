package co.edu.uniquindio.FitZone.service.interfaces;

import co.edu.uniquindio.FitZone.dto.request.CreateMembershipRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipResponse;

public interface IMembershipService {

    MembershipResponse createMembership(CreateMembershipRequest request);


}
