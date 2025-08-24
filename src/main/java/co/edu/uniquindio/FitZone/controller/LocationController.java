package co.edu.uniquindio.FitZone.controller;

import co.edu.uniquindio.FitZone.service.interfaces.ILocationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para gestionar las sedes.
 */
@RestController
@RequestMapping("/locations")
public class LocationController {

    private final ILocationService locationService;

    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }


}
