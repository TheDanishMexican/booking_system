package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceResponse;
import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.dto.TimeSlotResponse;
import booking.system.hovedopgave.exception.BookingException;
import booking.system.hovedopgave.exception.TimeSlotException;
import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.repository.BookingRepository;
import booking.system.hovedopgave.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final OfferedServiceService offeredServiceService;
    private final BookingRepository bookingRepository;

    public TimeSlotService(TimeSlotRepository timeSlotRepository, OfferedServiceService offeredServiceService, BookingRepository bookingRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.offeredServiceService = offeredServiceService;
        this.bookingRepository = bookingRepository;
    }

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new TimeSlotException("TimeSlot not found with id: " + id));
    }

    public TimeSlotResponse createTimeSlot(TimeSlotRequest request) {
        try {
            OfferedService service = offeredServiceService.getServiceById(request.offeredServiceId());

            validateTimeSlotRequest(request);

            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setStartTime(request.startTime());
            timeSlot.setEndTime(calculateEndTime(request.duration(), request.startTime()));
            timeSlot.setOfferedService(service);
            timeSlot.setLocation(request.location());
            timeSlot.setMaxParticipants(request.maxParticipants());
            timeSlot.setIsAvailable(true);
            timeSlot = timeSlotRepository.save(timeSlot);

            return toDto(timeSlot);

        } catch (Exception e) {
            throw new TimeSlotException("Time slot creation failed: " + e.getMessage(), e);
        }
    }

    public void deleteTimeSlot(Long id) {
        if (!timeSlotRepository.existsById(id)) {
            throw new TimeSlotException("TimeSlot with ID " + id + " does not exist");
        }
        timeSlotRepository.deleteById(id);
    }

    public List<TimeSlot> getFutureAvailableTimeSlotsByServiceId(Long serviceId) {
        return timeSlotRepository.findAllAvailableAndFutureByServiceId(serviceId);
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

    public void setTimeSlotAvailable(Long timeSlotId) {
        TimeSlot timeSlot = getTimeSlotById(timeSlotId);
        timeSlot.setIsAvailable(true);
        timeSlotRepository.save(timeSlot);
    }

    public void checkTimeSlotAvailability(TimeSlot timeSlot) {
        if (!timeSlot.getIsAvailable()) {
            throw new TimeSlotException("Time slot full, not available for booking");
        }
    }

    public void validateTimeSlotRequest(TimeSlotRequest request) {
        if (request.duration() == null || request.duration() <= 0) {
            throw new TimeSlotException("Invalid duration: must be greater than zero");
        }
        if (request.startTime().isBefore(java.time.LocalDateTime.now())) {
            throw new TimeSlotException("Invalid time slot: start time is in the past");
        }
        if (request.maxParticipants() <= 0) {
            throw new TimeSlotException("Invalid number of participants: must be greater than zero");
        }
    }

    public TimeSlotResponse toDto(TimeSlot timeSlot) {
        OfferedServiceResponse offeredServiceResponse = offeredServiceService.toDto(timeSlot.getOfferedService());

        return new TimeSlotResponse(
                timeSlot.getId(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                timeSlot.getLocation(),
                timeSlot.getMaxParticipants(),
                timeSlot.getIsAvailable(),
                offeredServiceResponse
        );
    }

    public LocalDateTime calculateEndTime(Integer duration, LocalDateTime startTime) {
        return startTime.plusMinutes(duration);
    }
}

