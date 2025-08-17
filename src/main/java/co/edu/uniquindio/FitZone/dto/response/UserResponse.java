package co.edu.uniquindio.FitZone.dto.response;

import co.edu.uniquindio.FitZone.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO RESPONSE - Para las respuestas REST
 * Contiene los datos de un usuario que se quieren enviar después de una operación
 * @param idUser
 * @param firstName
 * @param lastName
 * @param email
 * @param phoneNumber
 * @param userRole
 * @param createdAt
 */
public record UserResponse(

        Long idUser,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        UserRole userRole,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt

) {
}
