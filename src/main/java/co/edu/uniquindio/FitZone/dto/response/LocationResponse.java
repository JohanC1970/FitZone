package co.edu.uniquindio.FitZone.dto.response;

public record LocationResponse(

        Long idLocation,
        String name,
        String address,
        String phoneNumber,
        Boolean isActive
) {
}
