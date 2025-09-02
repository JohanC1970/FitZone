package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.LocationRequest;
import co.edu.uniquindio.FitZone.dto.response.LocationResponse;
import co.edu.uniquindio.FitZone.exception.FranchiseNotFoundException;
import co.edu.uniquindio.FitZone.exception.LocationNotFoundException;
import co.edu.uniquindio.FitZone.exception.ResourceAlreadyExistsException;
import co.edu.uniquindio.FitZone.model.entity.Franchise;
import co.edu.uniquindio.FitZone.model.entity.Location;
import co.edu.uniquindio.FitZone.repository.FranchiseRepository;
import co.edu.uniquindio.FitZone.repository.LocationRepository;
import co.edu.uniquindio.FitZone.service.interfaces.ILocationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio para gestionar las sedes de la franquicia FitZone.
 * Proporciona métodos para registrar, actualizar, eliminar y consultar sedes.
 * Utiliza repositorios para interactuar con la base de datos y maneja excepciones
 * específicas para casos como sede no encontrada o nombre ya registrado.
 */
@Service
public class LocationServiceImpl implements ILocationService {

    private final LocationRepository locationRepository;
    private final FranchiseRepository franchiseRepository;

    public LocationServiceImpl(LocationRepository locationRepository, FranchiseRepository franchiseRepository) {
        this.locationRepository = locationRepository;
        this.franchiseRepository = franchiseRepository;
    }


    @Override
    public LocationResponse registerLocation(LocationRequest request) {

        Franchise defaultFranchise = franchiseRepository.findByName("FitZone")
                .orElseThrow(()-> new FranchiseNotFoundException("Franquicia 'FitZone' no encontrada"));


        if(locationRepository.existsByName(request.name())){
            throw new ResourceAlreadyExistsException("El nombre de la sede ya se encuentra registrado");
        }

        if(locationRepository.existsByAddress(request.address())){
            throw  new ResourceAlreadyExistsException("La dirección ingresada ya corresponde a una sede registrada");
        }

        Location location = new Location();
        location.setName(request.name());
        location.setAddress(request.address());
        location.setPhoneNumber(request.phoneNumber());

        //Asignamos la franquicia por defecto a la nueva sede
        location.setFranchise(defaultFranchise);
        location.setMembers(new ArrayList<>());

        Location savedLocation = locationRepository.save(location);

        return new LocationResponse(
                savedLocation.getIdLocation(),
                savedLocation.getName(),
                savedLocation.getAddress(),
                savedLocation.getPhoneNumber(),
                savedLocation.getIsActive()
        );
    }


    @Override
    public LocationResponse updateLocation(Long idLocation, LocationRequest request) {

        Location existingLocation = locationRepository.findById(idLocation)
                .orElseThrow(() -> new LocationNotFoundException("Sede no encontrada"));

        if(!existingLocation.getName().equals(request.name()) && locationRepository.existsByName(request.name())){
            throw new ResourceAlreadyExistsException("El nuevo nombre de la sede ya se encuentra registrado");
        }

        if(!existingLocation.getAddress().equals(request.address()) && locationRepository.existsByAddress(request.address())){
            throw new ResourceAlreadyExistsException("La nueva dirección de la sede ya se encuentra asignada a otra sede");
        }

        if(!existingLocation.getPhoneNumber().equals(request.phoneNumber()) && locationRepository.existsByPhoneNumber(request.phoneNumber())){
            throw new ResourceAlreadyExistsException("El nuevo teléfono de la sede ya se encuentra asignado a otra sede");
        }

        existingLocation.setName(request.name());
        existingLocation.setAddress(request.address());
        existingLocation.setPhoneNumber(request.phoneNumber());

        Location updatedLocation = locationRepository.save(existingLocation);

        return new LocationResponse(
                updatedLocation.getIdLocation(),
                updatedLocation.getName(),
                updatedLocation.getAddress(),
                updatedLocation.getPhoneNumber(),
                updatedLocation.getIsActive()
        );
    }

    @Override
    public void deleteLocation(Long idLocation) {

        Location location = locationRepository.findById(idLocation)
                .orElseThrow( () -> new LocationNotFoundException("Sede no encontrada"));

        location.setIsActive(false);
        locationRepository.save(location);
    }

    @Override
    public LocationResponse getLocationById(Long idLocation) {
        Location location = locationRepository.findById(idLocation)
                .orElseThrow( () -> new LocationNotFoundException("Sede no encontrada"));

        return new LocationResponse(
                location.getIdLocation(),
                location.getName(),
                location.getAddress(),
                location.getPhoneNumber(),
                location.getIsActive()
        );
    }

    @Override
    public List<LocationResponse> getAllLocations() {
        List<Location> locations = locationRepository.findByIsActiveTrue();

        return locations.stream()
                .map(location -> new LocationResponse(
                        location.getIdLocation(),
                        location.getName(),
                        location.getAddress(),
                        location.getPhoneNumber(),
                        location.getIsActive()
                )).toList();
    }

    @Override
    public LocationResponse getLocationByPhoneNumber(String phoneNumber) {
        Location location = locationRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow( () -> new LocationNotFoundException("Sede no encontrada"));

        return new LocationResponse(
                location.getIdLocation(),
                location.getName(),
                location.getAddress(),
                location.getPhoneNumber(),
                location.getIsActive()
        );
    }

    @Override
    public LocationResponse getLocationAddress(String address) {
        Location location = locationRepository.findByAddress(address)
                .orElseThrow( () -> new LocationNotFoundException("Sede no encontrada"));

        return new LocationResponse(
                location.getIdLocation(),
                location.getName(),
                location.getAddress(),
                location.getPhoneNumber(),
                location.getIsActive()
        );
    }

    @Override
    public LocationResponse getByName(String name) {

        Location location = locationRepository.findByName(name)
                .orElseThrow( () -> new LocationNotFoundException("Sede no encontrada"));

        return new LocationResponse(
                location.getIdLocation(),
                location.getName(),
                location.getAddress(),
                location.getPhoneNumber(),
                location.getIsActive()
        );
    }
}
