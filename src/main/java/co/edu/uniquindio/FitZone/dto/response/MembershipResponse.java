package co.edu.uniquindio.FitZone.dto.response;

import co.edu.uniquindio.FitZone.model.entity.MembershipType;
import co.edu.uniquindio.FitZone.model.enums.MembershipStatus;
import co.edu.uniquindio.FitZone.model.enums.MembershipTypeName;

import java.time.LocalDate;

public record MembershipResponse(

        Long id,
        Long userId,
        MembershipTypeName membershipTypeName,
        Long locationId,
        LocalDate startDate,
        LocalDate endDate,
        MembershipStatus status
) {
}
