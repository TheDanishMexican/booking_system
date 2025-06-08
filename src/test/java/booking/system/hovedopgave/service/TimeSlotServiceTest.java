package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.dto.TimeSlotResponse;
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
    private OfferedServiceService offeredServiceService;

    //Happy case path
    @Test
    public void testCreateValidTimeSlot() {
        OfferedServiceRequest OfferedServiceRequest = new OfferedServiceRequest(
                "Massage",
                "Relaxing massage session",
                75.00
        );

        OfferedService offeredService = offeredServiceService.createService(OfferedServiceRequest);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                offeredService.getId(),
                "Room 101",
                1
        );

        TimeSlotResponse timeSlot = timeSlotService.createTimeSlot(timeSlotRequest);

        assertNotNull(timeSlot.id());
        assertEquals(offeredService.getId(), timeSlot.offeredServiceId());
        assertTrue(timeSlot.startTime().isBefore(timeSlot.endTime()));
    }

    //Failure case path
    @Test
    public void testCreateTimeSlotFailsWhenServiceIdDoesNotExist() {
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                9999L,
                "Room 102",
                1
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().contains("Time slot creation failed"),  "Should fail because service does not exist");
    }

    //Edge case path
    @Test
    public void testCreateTimeSlotFailsWhenStartAfterEnd() {
        OfferedServiceRequest request = new OfferedServiceRequest(
                "Yoga",
                "Relaxing yoga session",
                60.00
        );

       OfferedService offeredService = offeredServiceService.createService(request);

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(
                LocalDateTime.now().plusDays(2).plusHours(1),
                LocalDateTime.now().plusDays(2),
                offeredService.getId(),
                "Room 103",
                1
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            timeSlotService.createTimeSlot(timeSlotRequest);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("start time"), "Should fail because start time is after end time");
    }


}

