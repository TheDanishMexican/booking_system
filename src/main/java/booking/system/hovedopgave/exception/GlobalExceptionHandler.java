package booking.system.hovedopgave.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors triggered by @Valid annotations.
     * Returns a map of field names and corresponding validation error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles general booking-related exceptions.
     * Returns a single error message with HTTP 400 Bad Request.
     */
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, String>> handleBookingException(BookingException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles admin-related exceptions.
     * Returns a single error message with HTTP 400 Bad Request.
     */
    @ExceptionHandler(AdminException.class)
    public ResponseEntity<Map<String, String>> handleAdminException(AdminException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions related to timeslots.
     * Returns a single error message with HTTP 400 Bad Request.
     */
    @ExceptionHandler(TimeSlotException.class)
    public ResponseEntity<Map<String, String>> handleTimeSlotException(TimeSlotException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions related to offered services.
     * Returns a single error message with HTTP 400 Bad Request.
     */
    @ExceptionHandler(OfferedServiceException.class)
    public ResponseEntity<Map<String, String>> handleOfferedServiceException(OfferedServiceException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
