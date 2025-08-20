package co.edu.uniquindio.FitZone.service.interfaces;

import co.edu.uniquindio.FitZone.dto.request.LocationRequest;
import co.edu.uniquindio.FitZone.dto.response.LocationResponse;

import java.util.List;

public interface ILocationService {

    LocationResponse registerLocation(LocationRequest request);

    LocationResponse updateLocation(Long idLocation, LocationRequest request);

    void deleteLocation(Long idLocation);

    LocationResponse getLocationById(Long idLocation);

    List<LocationResponse> getAllLocations();

    LocationResponse getLocationByPhoneNumber(String phoneNumber);

    LocationResponse getLocationAddress(String address);

    LocationResponse getByName(String name);


}
