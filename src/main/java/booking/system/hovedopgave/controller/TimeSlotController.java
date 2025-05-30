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

    @GetMapping
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

}
