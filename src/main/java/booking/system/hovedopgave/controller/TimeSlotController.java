package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotService.getAllTimeSlots();
    }

    @GetMapping("/{id}")
    public TimeSlot getTimeSlotById(@PathVariable Long id) {
        return timeSlotService.getTimeSlotById(id);
    }

    @GetMapping("/service/{serviceId}")
    public List<TimeSlot> getTimeSlotsByServiceId(@PathVariable Long serviceId) {
        return timeSlotService.getTimeSlotsByServiceId(serviceId);
    }

    @PostMapping
    public TimeSlot createTimeSlot(@RequestBody TimeSlot timeSlot) {
        return timeSlotService.createTimeSlot(timeSlot);
    }

    @DeleteMapping("/{id}")
    public void deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
    }
}

