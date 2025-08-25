package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.TimeslotRequest;
import co.edu.uniquindio.FitZone.dto.response.FranchiseResponse;
import co.edu.uniquindio.FitZone.exception.FranchiseNotFoundException;
import co.edu.uniquindio.FitZone.model.entity.Franchise;
import co.edu.uniquindio.FitZone.model.entity.Timeslot;
import co.edu.uniquindio.FitZone.repository.FranchiseRepository;
import co.edu.uniquindio.FitZone.service.interfaces.IFranchiseService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Implementación del servicio para gestionar franquicias.
 * Proporciona métodos para actualizar los horarios de una franquicia.
 */
@Service
public class FranchiseServiceImpl implements IFranchiseService {

    private final FranchiseRepository franchiseRepository;

    public FranchiseServiceImpl(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }


    @Override
    public FranchiseResponse updateTimeslots(Set<TimeslotRequest> timeslots) {

        Franchise franchise = franchiseRepository.findByName("FitZone")
                .orElseThrow(() -> new FranchiseNotFoundException(" Franquicia no encontrada"));

        // Limpiar los horarios antiguos y agregar los nuevos
        franchise.getTimeslots().clear();

        for(TimeslotRequest request : timeslots) {
            Timeslot timeslot = new Timeslot();
            timeslot.setDay(request.day());
            timeslot.setOpenTime(request.openTime());
            timeslot.setCloseTime(request.closeTime());
            timeslot.setFranchise(franchise);
            franchise.getTimeslots().add(timeslot);
        }

        Franchise updatedFranchise = franchiseRepository.save(franchise);

        return new FranchiseResponse(
                updatedFranchise.getIdFranchise(),
                updatedFranchise.getName(),
                updatedFranchise.getTimeslots()
        );
    }


}
