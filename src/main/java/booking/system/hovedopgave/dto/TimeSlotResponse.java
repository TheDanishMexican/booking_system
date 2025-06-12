package booking.system.hovedopgave.dto;

import java.time.OffsetDateTime;

public record TimeSlotResponse(
        Long id,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        String location,
        Integer maxParticipants,
        Boolean isAvailable,
        OfferedServiceResponse offeredService
) {}


