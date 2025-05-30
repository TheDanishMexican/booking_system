package booking.system.hovedopgave.service;

import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;
    @Autowired
    private OfferedServiceService offeredServiceService;

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found with id: " + id));
    }

    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        OfferedService offeredService = offeredServiceService.getServiceById(timeSlot.getOfferedService().getId());
        timeSlot.setOfferedService(offeredService);
        return timeSlotRepository.save(timeSlot);
    }

    public void deleteTimeSlot(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found with id: " + id));
        timeSlotRepository.delete(timeSlot);
    }

    public List<TimeSlot> getTimeSlotsByServiceId(Long serviceId) {
        return timeSlotRepository.findByOfferedServiceId(serviceId);
    }

}

