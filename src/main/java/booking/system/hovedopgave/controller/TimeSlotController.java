package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.dto.TimeSlotResponse;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.service.TimeSlotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> getAllTimeSlots() {
        List<TimeSlot> slots = timeSlotService.getAllTimeSlots();
        List<TimeSlotResponse> responses = slots.stream()
                .map(timeSlotService::toDto)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> getTimeSlotById(@PathVariable Long id) {
        TimeSlot slot = timeSlotService.getTimeSlotById(id);
        return ResponseEntity.ok(timeSlotService.toDto(slot));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<TimeSlotResponse>> getFutureAvailableTimeSlotsByServiceId(@PathVariable Long serviceId) {
        List<TimeSlot> slots = timeSlotService.getFutureAvailableTimeSlotsByServiceId(serviceId);
        List<TimeSlotResponse> responses = slots.stream()
                .map(timeSlotService::toDto)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<TimeSlotResponse> createTimeSlot(@Valid @RequestBody TimeSlotRequest timeSlotRequest) {
        TimeSlotResponse created = timeSlotService.createTimeSlot(timeSlotRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }
}


