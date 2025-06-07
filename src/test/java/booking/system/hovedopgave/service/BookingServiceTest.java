package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.BookingRequest;
import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private OfferedServiceRepository offeredServiceRepository;

    @Test
    public void testCreateValidBooking() {
        // Create and save a service
        OfferedService service = new OfferedService();
        service.setName("Test Service");
        offeredServiceRepository.save(service);

        // Create and save a time slot linked to the service
        TimeSlot slot = new TimeSlot();
        slot.setStartTime(LocalDateTime.now().plusDays(1));
        slot.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        slot.setOfferedService(service);
        timeSlotRepository.save(slot);

        // Build a booking request
        BookingRequest request = new BookingRequest(
                "test@example.com",
                "Daniel",
                "12345678",
                slot.getId()
        );

        // Call service method
        Booking booking = bookingService.createBooking(request);

        // Assertions
        assertNotNull(booking.getId());
        assertEquals("Daniel", booking.getName());
        assertEquals("test@example.com", booking.getEmail());
        assertEquals(slot.getId(), booking.getTimeSlot().getId());
    }

    @Test
    public void testCreateBookingFailsWhenTimeSlotMissing() {
        BookingRequest request = new BookingRequest(
                "test@example.com",
                "Daniel",
                "12345678",
                9999L // use an ID that doesn’t exist
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(request);
        });

        assertTrue(exception.getMessage().contains("Booking failed"));
    }

}
