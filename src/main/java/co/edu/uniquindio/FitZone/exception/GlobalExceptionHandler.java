package co.edu.uniquindio.FitZone.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Captura todas las excepciones registradas y las transforma en un formato de respuesta para
 * notificar el error
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception){
        return buildResponseEntity(HttpStatus.CONFLICT, "Conflicto", exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception){
        return buildResponseEntity(HttpStatus.NOT_FOUND, "No encontrado", exception.getMessage());
    }

    // Manejo de errores de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Validación fallida", mensaje);
    }

    //Manejo genérico para cualquier excepcion no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception exception){
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", exception.getMessage());
    }


    /**
     * Método que me arma el cuerpo de la respuesta
     * @param status Estado del error
     * @param error Que tipo de error se presento
     * @param message Mensaje que explica el error
     * @return
     */
    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }



}
