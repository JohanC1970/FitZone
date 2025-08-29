package co.edu.uniquindio.FitZone.repository;

import co.edu.uniquindio.FitZone.dto.response.MembershipResponse;
import co.edu.uniquindio.FitZone.model.entity.Membership;
import org.springframework.data.repository.CrudRepository;

public interface MembershipRepository extends CrudRepository<Membership, Long> {

}
