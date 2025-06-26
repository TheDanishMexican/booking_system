package booking.system.hovedopgave.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TimeSlotRequest(
        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        @Positive(message = "Duration must be a positive number")
        @NotNull(message = "Duration is required")
        Integer duration,

        @NotNull(message = "Service ID is required")
        Long offeredServiceId,

        @NotBlank(message = "Location is required")
        String location,

        @Positive(message = "Max participants must be a positive number")
        @NotNull(message = "Max participants is required")
        Integer maxParticipants
) {}


