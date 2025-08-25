package co.edu.uniquindio.FitZone.dto.response;

import co.edu.uniquindio.FitZone.model.entity.Timeslot;

import java.util.List;
import java.util.Set;

public record FranchiseResponse(

        Long idFranchise,
        String name,
        Set<Timeslot> timeslots
) {
}
