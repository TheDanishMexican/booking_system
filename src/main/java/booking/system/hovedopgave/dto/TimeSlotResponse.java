package booking.system.hovedopgave.dto;

import java.time.LocalDateTime;

public record TimeSlotResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String location,
        Integer maxParticipants,
        Boolean isAvailable,
        Long offeredServiceId
) {}

