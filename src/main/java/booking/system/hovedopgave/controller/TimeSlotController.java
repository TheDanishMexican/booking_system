package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.service.TimeSlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots() {
        List<TimeSlot> slots = timeSlotService.getAllTimeSlots();

        return ResponseEntity.ok()
                .body(slots);
    }

    @GetMapping("/{id}")
    public TimeSlot getTimeSlotById(@PathVariable Long id) {
        return timeSlotService.getTimeSlotById(id);
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlotsByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok().body(timeSlotService.getAvailableTimeSlotsByServiceId(serviceId));
    }

    @PostMapping
    public ResponseEntity<TimeSlot> createTimeSlot(@Valid @RequestBody TimeSlotRequest timeSlotRequest) {
        TimeSlot created = timeSlotService.createTimeSlot(timeSlotRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }


    @DeleteMapping("/{id}")
    public void deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
    }
}

