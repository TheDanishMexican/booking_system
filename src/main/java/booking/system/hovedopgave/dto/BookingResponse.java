package booking.system.hovedopgave.dto;

public record BookingResponse(
        Long id,
        String name,
        String email,
        String phone,
        Long timeSlotId,
        String status,
        Boolean paid
) {}

