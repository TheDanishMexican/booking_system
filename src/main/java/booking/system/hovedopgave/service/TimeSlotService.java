package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.TimeSlotRequest;
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

    public TimeSlot createTimeSlot(TimeSlotRequest request) {
        try {
            OfferedService service = offeredServiceService.getServiceById(request.offeredServiceId());

            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setStartTime(request.startTime());
            timeSlot.setEndTime(request.endTime());
            timeSlot.setOfferedService(service);
            timeSlot.setLocation(request.location());
            return timeSlotRepository.save(timeSlot);

        } catch (Exception e) {
            throw new RuntimeException("Time slot creation failed: " + e.getMessage());
        }
    }


    public void deleteTimeSlot(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found with id: " + id));
        timeSlotRepository.delete(timeSlot);
    }

    public List<TimeSlot> getTimeSlotsByServiceId(Long serviceId) {
        return timeSlotRepository.findAllByOfferedService_IdAndIsAvailableTrue(serviceId);
    }

    public void markAsUnavailableIfFull(TimeSlot timeSlot) {
        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);
    }

}

