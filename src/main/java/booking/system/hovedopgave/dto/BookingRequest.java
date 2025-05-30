package booking.system.hovedopgave.dto;

public record BookingRequest (
     String customerEmail,
     Long timeSlotId) {}
