package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-slots")
public class TimeSlotController {

    @Autowired
    TimeSlotRepository timeSlotRepository;

    @PostMapping
    public TimeSlot createTimeSlot(@RequestBody TimeSlot timeSlot) {
        timeSlot.setIsBooked(false);
        return timeSlotRepository.save(timeSlot);
    }

    @GetMapping
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    @PutMapping("/{id}/book")
    public TimeSlot bookTimeSlot(@PathVariable Long id) {
        return timeSlotRepository.findById(id).map(timeSlot -> {
            timeSlot.setIsBooked(true);
            return timeSlotRepository.save(timeSlot);
        }).orElseThrow(() -> new RuntimeException("Time slot not found"));
    }

    @GetMapping("/available")
    public List<TimeSlot> getAvailableTimeSlots() {
        return timeSlotRepository.findByIsBookedFalse();
    }
}
