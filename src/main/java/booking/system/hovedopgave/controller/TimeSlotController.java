package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.dto.TimeSlotResponse;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.service.TimeSlotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timeslots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @GetMapping
    public List<TimeSlotResponse> getAllTimeSlots(@RequestParam(defaultValue = "future") String type) {
        if ("past".equals(type)) {
            return timeSlotService.getPastTimeSlots();
        }
        return timeSlotService.getAllFutureTimeSlots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> getTimeSlotById(@PathVariable Long id) {
        TimeSlot slot = timeSlotService.getTimeSlotById(id);
        return ResponseEntity.ok(timeSlotService.toDto(slot));
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

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlotsByServiceAndDate(
            @PathVariable Long serviceId,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<TimeSlotResponse> slots = timeSlotService.getAvailableTimeSlotsByServiceIdAndDate(serviceId, localDate);

        return ResponseEntity.ok(slots);
    }

}


