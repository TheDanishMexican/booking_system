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

    // Injecting the BookingService to test booking creation and related logic
    @Autowired
    private BookingService bookingService;

    // Injecting TimeSlotService to create and manage time slots for bookings
    @Autowired
    private TimeSlotService timeSlotService;

    // Injecting OfferedServiceService to create offered services which time slots will be linked to
    @Autowired
    private OfferedServiceService offeredServiceService;

    // Injecting AdminRepository to fetch admin user for authentication setup
    @Autowired
    private AdminRepository adminRepository;

    /**
     * This method runs before each test to set up the Spring Security context with
     * a real authenticated admin user. This simulates an authenticated environment
     * so that service methods relying on authentication work correctly during tests.
     */
    @BeforeEach
    public void setupAuthentication() {
        Admin admin = adminRepository.findByEmail("daniel@email.com").orElseThrow();
        AdminDetails adminDetails = new AdminDetails(admin);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                adminDetails, null, adminDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Happy path test: verifies that a valid booking can be created successfully
     * when all required inputs are valid and the user is authenticated.
     */
    @Test
    public void testCreateValidBooking_WithAuthentication() {
        // Create a valid offered service to link to the time slot
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Test Service",
                "This is a test service",
                100.0
        );

        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        // Create a valid time slot linked to the offered service
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(1),  // Start time in the future
                90,                               // Duration in minutes
                offeredService.id(),
                "Test Location",
                1                                 // Max participants
        );

        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        // Create a booking request linked to the time slot
        BookingRequest bookingRequest = new BookingRequest(
                "test@example.com",
                "Daniel",
                "12345678",
                timeSlot.id()
        );

        // Perform the booking creation
        BookingResponse booking = bookingService.createBooking(bookingRequest);

        // Verify the booking was created with expected details
        assertNotNull(booking.id());
        assertEquals("Daniel", booking.name());
        assertEquals("test@example.com", booking.email());
        assertEquals(timeSlot.id(), booking.timeSlot().id());
    }

    /**
     * Failure case test: verifies that booking creation fails and throws a BookingException
     * when trying to create a booking with a non-existent (missing) time slot ID.
     */
    @Test
    public void testCreateBookingFailsWhenTimeSlotMissing_WithAuthentication() {
        BookingRequest request = new BookingRequest(
                "test@example.com",
                "Daniel",
                "12345678",
                9999L  // Non-existent time slot ID
        );

        // Expect BookingException due to missing time slot
        Exception exception = assertThrows(BookingException.class, () -> {
            bookingService.createBooking(request);
        });

        // Check that the exception message contains "Booking failed"
        assertTrue(exception.getMessage().contains("Booking failed"), "Should fail when time slot is missing");
    }

    /**
     * Edge case test: verifies that booking creation fails when trying to book a time slot
     * that is already full (i.e., max participants reached), expecting a BookingException.
     */
    @Test
    public void testCreateBookingFailsWhenTimeSlotIsFull_WithAuthentication() {
        // Create a valid offered service for the time slot
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Test Service1",
                "This is a test service",
                100.0
        );
        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        // Create a time slot with max participants = 1
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(2),
                90,
                offeredService.id(),
                "Test Location",
                1
        );
        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        // First booking should succeed (fills the slot)
        BookingRequest bookingRequest1 = new BookingRequest(
                "test1@example.com",
                "Daniel",
                "12345678",
                timeSlot.id()
        );
        bookingService.createBooking(bookingRequest1);

        // Second booking should fail because the slot is full
        BookingRequest bookingRequest2 = new BookingRequest(
                "test2@example.com",
                "Anna",
                "87654321",
                timeSlot.id()
        );

        // Expect BookingException due to full timeslot
        Exception exception = assertThrows(BookingException.class, () -> bookingService.createBooking(bookingRequest2));

        // Check exception message mentions the slot being full or unavailable
        assertTrue(exception.getMessage().toLowerCase().contains("full")
                        || exception.getMessage().toLowerCase().contains("not available"),
                "Should fail because the timeslot is full or unavailable");
    }
}
