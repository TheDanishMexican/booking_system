package booking.system.hovedopgave.dto;

public record OfferedServiceResponse(
        Long id,
        String name,
        String description,
        Double price,
        Long adminId
) {}
