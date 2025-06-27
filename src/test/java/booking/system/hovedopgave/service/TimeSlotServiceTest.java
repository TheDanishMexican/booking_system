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
    public void testCreateValidTimeSlot_WithAuthentication() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest("Massage", "Relaxing massage session", 75.00);

        OfferedServiceResponse offeredService = offeredServiceService.createService(offeredServiceRequest);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalDateTime.now().plusDays(20), 90, offeredService.id(), "Room 101", 1);

        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        assertNotNull(timeSlot.id());
        assertEquals(offeredService.id(), timeSlot.offeredService().id());
        assertTrue(timeSlot.startTime().isBefore(timeSlot.endTime()));
    }

    @Test
    public void testCreateTimeSlotFailsWhenServiceIdDoesNotExist_WithAuthentication() {
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalDateTime.now().plusHours(1), 90, 9999L, "Room 102", 1);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().contains("Time slot creation failed"), "Should fail because service does not exist");
    }

    @Test
    public void testCreateTimeSlotFailsWhenDurationIsNegative_WithAuthentication() {
        OfferedServiceRequest request = new OfferedServiceRequest("Yoga", "Relaxing yoga session", 60.00);

        OfferedServiceResponse offeredService = offeredServiceService.createService(request);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalDateTime.now().plusDays(2).plusHours(1), -90, offeredService.id(), "Room 103", 1);

        Exception exception = assertThrows(TimeSlotException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("duration"), "Should fail because duration is negative");
    }

    @Test
    public void testCreateTimeSlotFailsWhenStartTimeIsInPast_WithAuthentication() {
        OfferedServiceRequest request = new OfferedServiceRequest("Meditation", "Mindfulness and relaxation", 40.00);
        OfferedServiceResponse offeredService = offeredServiceService.createService(request);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalDateTime.now().minusHours(1), 60, offeredService.id(), "Room 105", 10);

        Exception exception = assertThrows(TimeSlotException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("start time"), "Should fail because start time is in the past");
    }
}
