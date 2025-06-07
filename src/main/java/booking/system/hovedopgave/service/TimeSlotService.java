package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.exception.BookingException;
import booking.system.hovedopgave.exception.TimeSlotException;
import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.repository.BookingRepository;
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
    @Autowired
    private BookingRepository bookingRepository;

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new TimeSlotException("TimeSlot not found with id: " + id));
    }

    public TimeSlot createTimeSlot(TimeSlotRequest request) {
        try {
            OfferedService service = offeredServiceService.getServiceById(request.offeredServiceId());

            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setStartTime(request.startTime());
            timeSlot.setEndTime(request.endTime());
            timeSlot.setOfferedService(service);
            timeSlot.setLocation(request.location());
            timeSlot.setMaxParticipants(request.maxParticipants());
            timeSlot.setIsAvailable(true);
            return timeSlotRepository.save(timeSlot);

        } catch (Exception e) {
            throw new TimeSlotException("Time slot creation failed: ", e);
        }
    }

    public void deleteTimeSlot(Long id) {
        if (!timeSlotRepository.existsById(id)) {
            throw new TimeSlotException("TimeSlot with ID " + id + " does not exist");
        }
        timeSlotRepository.deleteById(id);
    }

    public List<TimeSlot> getAvailableTimeSlotsByServiceId(Long serviceId) {
        return timeSlotRepository.findAllByOfferedService_IdAndIsAvailableTrue(serviceId);
    }

    private void markAsUnavailableIfFull(TimeSlot timeSlot) {
        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);
    }

    //Had to use the BookingRepository directly to avoid circular dependency issues
    public void setTimeSlotUnavailableIfFull(TimeSlot timeSlot) {
        if (bookingRepository.countByTimeSlotId(timeSlot.getId()) >= timeSlot.getMaxParticipants()) {
            markAsUnavailableIfFull(timeSlot);
        }
    }

    public void checkTimeSlotAvailability(TimeSlot timeSlot) {
        if (!timeSlot.getIsAvailable()) {
            throw new TimeSlotException("Time slot full, not available for booking");
        }
    }

}

