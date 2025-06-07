package booking.system.hovedopgave.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TimeSlotRequest(
        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        LocalDateTime endTime,

        @NotNull(message = "Service ID is required")
        Long offeredServiceId,

        @NotBlank(message = "Location is required")
        String location,

        @NotNull(message = "Max participants is required")
        Integer maxParticipants


) {
}


