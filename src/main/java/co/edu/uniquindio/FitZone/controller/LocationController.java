package co.edu.uniquindio.FitZone.controller;

import co.edu.uniquindio.FitZone.dto.request.LocationRequest;
import co.edu.uniquindio.FitZone.dto.response.LocationResponse;
import co.edu.uniquindio.FitZone.service.interfaces.ILocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar las ubicaciones.
 * Proporciona endpoints para crear, actualizar y obtener ubicaciones.
 */
@RestController
@RequestMapping("/locations")
public class LocationController {

    private final ILocationService locationService;

    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los administradores pueden crear ubicaciones
    public ResponseEntity<LocationResponse> registerLocation(@RequestBody LocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.registerLocation(request));
    }

    @PutMapping("/{idLocation}")
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los administradores pueden actualizar ubicaciones
    public ResponseEntity<LocationResponse> updateLocation(@PathVariable Long idLocation, @RequestBody LocationRequest request) {
        return ResponseEntity.ok(locationService.updateLocation(idLocation, request));
    }

    @PutMapping("/{idLocation}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deactivateLocation(@PathVariable Long idLocation) {
        locationService.deleteLocation(idLocation);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idLocation}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long idLocation) {
        return ResponseEntity.ok(locationService.getLocationById(idLocation));
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/by-phone")
    public ResponseEntity<LocationResponse> getLocationByPhoneNumber(@RequestParam String phoneNumber) {
        return ResponseEntity.ok(locationService.getLocationByPhoneNumber(phoneNumber));
    }

    @GetMapping("/by-address")
    public ResponseEntity<LocationResponse> getLocationByAddress(@RequestParam String address) {
        return ResponseEntity.ok(locationService.getLocationAddress(address));
    }

    @GetMapping("/by-name")
    public ResponseEntity<LocationResponse> getLocationByName(@RequestParam String name) {
        return ResponseEntity.ok(locationService.getByName(name));
    }

}
