package booking.system.hovedopgave.dto;

public record BookingResponse(
        Long id,
        String name,
        String email,
        String phone,
        TimeSlotResponse timeSlot,
        String status,
        Boolean paid
) {}

