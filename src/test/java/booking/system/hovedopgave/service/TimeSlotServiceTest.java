package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.dto.OfferedServiceResponse;
import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.dto.TimeSlotResponse;
import booking.system.hovedopgave.exception.TimeSlotException;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.security.AdminDetails;
import booking.system.hovedopgave.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TimeSlotServiceTest {

    // Injecting the TimeSlotService to test creation and management of time slots
    @Autowired
    private TimeSlotService timeSlotService;

    // Injecting OfferedServiceService to create offered services linked to time slots
    @Autowired
    private OfferedServiceService offeredServiceService;

    // Injecting AdminRepository to retrieve an admin for authentication setup
    @Autowired
    private AdminRepository adminRepository;

    /**
     * Sets up the security context with an authenticated admin user before each test.
     * This simulates a logged-in admin to satisfy security requirements of service methods.
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
     * Happy path test: verifies successful creation of a valid time slot
     * linked to an offered service with proper attributes.
     */
    @Test
    public void testCreateValidTimeSlot_WithAuthentication() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Massage",
                "Relaxing massage session",
                75.00
        );

        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(2), // Start time in the future
                90,                             // Duration in minutes
                offeredService.id(),
                "Room 101",
                1                               // Max participants
        );

        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        assertNotNull(timeSlot.id());
        assertEquals(offeredService.id(), timeSlot.offeredService().id());
        assertTrue(timeSlot.startTime().isBefore(timeSlot.endTime()));
    }

    /**
     * Failure path test: verifies that creating a time slot with a non-existent
     * service ID results in an exception being thrown.
     */
    @Test
    public void testCreateTimeSlotFailsWhenServiceIdDoesNotExist_WithAuthentication() {
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusHours(1), // Start time in the near future
                90,
                9999L,                           // Non-existent service ID
                "Room 102",
                1
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().contains("Time slot creation failed"),  "Should fail because service does not exist");
    }

    /**
     * Edge case test: verifies that creating a time slot with a negative duration
     * throws a TimeSlotException indicating the invalid input.
     */
    @Test
    public void testCreateTimeSlotFailsWhenDurationIsNegative_WithAuthentication() {
        OfferedServiceRequest request = new OfferedServiceRequest(
                "Yoga",
                "Relaxing yoga session",
                60.00
        );

        OfferedServiceResponse offeredService = offeredServiceService.createService(request);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(2).plusHours(1),
                -90,                              // Invalid negative duration
                offeredService.id(),
                "Room 103",
                1
        );

        Exception exception = assertThrows(TimeSlotException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("duration"), "Should fail because duration is negative");
    }

    /**
     * Edge case test: verifies that creating a time slot with a start time
     * set in the past correctly throws a TimeSlotException.
     * This ensures the system enforces the business rule that time slots
     * must start in the future.
     */
    @Test
    public void testCreateTimeSlotFailsWhenStartTimeIsInPast_WithAuthentication() {
        OfferedServiceRequest request = new OfferedServiceRequest(
                "Meditation",
                "Mindfulness and relaxation",
                40.00
        );
        OfferedServiceResponse offeredService = offeredServiceService.createService(request);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().minusHours(1),  // start time in the past
                60,
                offeredService.id(),
                "Room 105",
                10
        );

        Exception exception = assertThrows(TimeSlotException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("start time"), "Should fail because start time is in the past");
    }
}
