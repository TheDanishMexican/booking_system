package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.repository.OfferedServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TimeSlotServiceTest {

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private OfferedServiceRepository offeredServiceRepository;

    @Test
    public void testCreateValidTimeSlot() {
        // Create and save an offered service
        OfferedService service = new OfferedService();
        service.setName("Massage");
        service = offeredServiceRepository.save(service);

        // Create request
        TimeSlotRequest request = new TimeSlotRequest(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                service.getId(),
                "Room 101",
                1
        );

        // Create time slot
        TimeSlot created = timeSlotService.createTimeSlot(request);

        // Assertions
        assertNotNull(created.getId());
        assertEquals(service.getId(), created.getOfferedService().getId());
        assertTrue(created.getStartTime().isBefore(created.getEndTime()));
    }

    @Test
    public void testCreateTimeSlotFailsWhenServiceIsMissing() {
        TimeSlotRequest request = new TimeSlotRequest(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                9999L,
                "Room 102",
                1
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            timeSlotService.createTimeSlot(request);
        });

        assertTrue(exception.getMessage().contains("Time slot creation failed"));
    }

}

