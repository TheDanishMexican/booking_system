package booking.system.hovedopgave.dto;

public record BookingRequest(
        String email,
        String name,
        String phone,
        Long timeSlotId
) {}

