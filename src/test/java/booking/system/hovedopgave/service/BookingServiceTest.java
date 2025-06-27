package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.*;
import booking.system.hovedopgave.exception.BookingException;
import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.*;
import booking.system.hovedopgave.security.AdminDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private OfferedServiceService offeredServiceService;

    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    public void setupAuthentication() {
        Admin admin = adminRepository.findByEmail("daniel@email.com").orElseThrow();
        AdminDetails adminDetails = new AdminDetails(admin);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testCreateValidBooking_WithAuthentication() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest("Test Service", "This is a test service", 100.0);

        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalDateTime.now().plusDays(10), 90, offeredService.id(), "Test Location", 1);

        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        BookingRequest bookingRequest = new BookingRequest("test@example.com", "Daniel", "12345678", timeSlot.id());

        BookingResponse booking = bookingService.createBooking(bookingRequest);

        assertNotNull(booking.id());
        assertEquals("Daniel", booking.name());
        assertEquals("test@example.com", booking.email());
        assertEquals(timeSlot.id(), booking.timeSlot().id());
    }

    @Test
    public void testCreateBookingFailsWhenTimeSlotMissing_WithAuthentication() {
        BookingRequest request = new BookingRequest("test@example.com", "Daniel", "12345678", 9999L);

        Exception exception = assertThrows(BookingException.class, () -> {
            bookingService.createBooking(request);
        });

        assertTrue(exception.getMessage().contains("Booking failed"), "Should fail when time slot is missing");
    }

    @Test
    public void testCreateBookingFailsWhenTimeSlotIsFull_WithAuthentication() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest("Test Service1", "This is a test service", 100.0);
        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalDateTime.now().plusDays(20), 90, offeredService.id(), "Test Location", 1);
        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        BookingRequest bookingRequest1 = new BookingRequest("test1@example.com", "Daniel", "12345678", timeSlot.id());
        bookingService.createBooking(bookingRequest1);

        BookingRequest bookingRequest2 = new BookingRequest("test2@example.com", "Anna", "87654321", timeSlot.id());

        Exception exception = assertThrows(BookingException.class, () -> bookingService.createBooking(bookingRequest2));

        assertTrue(exception.getMessage().toLowerCase().contains("full") || exception.getMessage().toLowerCase().contains("not available"), "Should fail because the timeslot is full or unavailable");
    }
}
