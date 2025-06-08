package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.*;
import booking.system.hovedopgave.exception.BookingException;
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
    private TimeSlotService timeSlotService;

    @Autowired
    private OfferedServiceService offeredServiceService;

    //Happy case path
    @Test
    public void testCreateValidBooking() {

        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Test Service",
                "This is a test service",
                100.0
        );

        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                offeredService.id(),
                "Test Location",
                1
        );


        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        BookingRequest bookingRequest = new BookingRequest(
                "test@example.com",
                "Daniel",
                "12345678",
                timeSlot.id()
        );

        BookingResponse booking = bookingService.createBooking(bookingRequest);

        assertNotNull(booking.id());
        assertEquals("Daniel", booking.name());
        assertEquals("test@example.com", booking.email());
        assertEquals(timeSlot.id(), booking.timeSlotId());
    }

    //Failure case path
    @Test
    public void testCreateBookingFailsWhenTimeSlotMissing() {
        BookingRequest request = new BookingRequest(
                "test@example.com",
                "Daniel",
                "12345678",
                9999L
        );

        Exception exception = assertThrows(BookingException.class, () -> {
            bookingService.createBooking(request);
        });

        assertTrue(exception.getMessage().contains("Booking failed"), "Should fail when time slot is missing");
    }

    //Edge case path
    @Test
    public void testCreateBookingFailsWhenTimeSlotIsFull() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Test Service1",
                "This is a test service",
                100.0
        );
        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                offeredService.id(),
                "Test Location",
                1
        );
        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        BookingRequest bookingRequest1 = new BookingRequest(
                "test1@example.com",
                "Daniel",
                "12345678",
                timeSlot.id()
        );
        bookingService.createBooking(bookingRequest1);

        BookingRequest bookingRequest2 = new BookingRequest(
                "test2@example.com",
                "Anna",
                "87654321",
                timeSlot.id()
        );

        Exception exception = assertThrows(BookingException.class, () -> bookingService.createBooking(bookingRequest2));

        assertTrue(exception.getMessage().toLowerCase().contains("full")
                        || exception.getMessage().toLowerCase().contains("not available"),
                "Should fail because the timeslot is full or unavailable");
    }
}
