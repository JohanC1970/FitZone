package co.edu.uniquindio.FitZone.controller;

import co.edu.uniquindio.FitZone.dto.request.TimeslotRequest;
import co.edu.uniquindio.FitZone.dto.response.FranchiseResponse;
import co.edu.uniquindio.FitZone.model.entity.Timeslot;
import co.edu.uniquindio.FitZone.service.interfaces.IFranchiseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Controlador para gestionar las franquicias.
 * Permite actualizar los horarios de una franquicia.
 * Solo los usuarios con rol ADMIN pueden acceder a este endpoint.
 */
@RestController
@RequestMapping("/franchises")
public class FranchiseController {

    private final IFranchiseService franchiseService;
    public FranchiseController(IFranchiseService franchiseService) {
        this.franchiseService = franchiseService;
    }

    @PutMapping("/timeslots")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FranchiseResponse> updateTimeslots(@RequestBody Set<TimeslotRequest> timeslots) {
        return ResponseEntity.ok(franchiseService.updateTimeslots(timeslots));

    }

}
