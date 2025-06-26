package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceResponse;
import booking.system.hovedopgave.dto.TimeSlotRequest;
import booking.system.hovedopgave.dto.TimeSlotResponse;
import booking.system.hovedopgave.exception.BookingException;
import booking.system.hovedopgave.exception.TimeSlotException;
import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.model.TimeSlot;
import booking.system.hovedopgave.repository.AdminRepository;
import booking.system.hovedopgave.repository.BookingRepository;
import booking.system.hovedopgave.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final OfferedServiceService offeredServiceService;
    private final BookingRepository bookingRepository;
    private final AdminService adminService;

    public TimeSlotService(TimeSlotRepository timeSlotRepository, OfferedServiceService offeredServiceService, BookingRepository bookingRepository, AdminService adminService) {
        this.timeSlotRepository = timeSlotRepository;
        this.offeredServiceService = offeredServiceService;
        this.bookingRepository = bookingRepository;
        this.adminService = adminService;
    }

    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id).orElseThrow(() -> new TimeSlotException("TimeSlot not found with id: " + id));
    }

    public void checkNoOverlappingTimeSlot(TimeSlotRequest request) {
        LocalDateTime startTime = request.startTime();
        LocalDateTime endTime = calculateEndTime(request.duration(), startTime);
        Long adminId = adminService.getCurrentAuthenticatedAdminId();
        if (timeSlotRepository.existsOverlappingTimeSlot(startTime, endTime, adminId)) {
            throw new TimeSlotException("Time slot overlaps with an existing time slot");
        }
    }

    private void checkAdminPermission(Long adminId, OfferedService service) {
        if (!service.getAdmin().getId().equals(adminId)) {
            throw new TimeSlotException("You do not have permission to create a time slot for this service");
        }
    }

    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotRequest request) {
        try {
            Long adminId = adminService.getCurrentAuthenticatedAdminId();

            OfferedService service = offeredServiceService.getServiceById(request.offeredServiceId());

            checkAdminPermission(adminId, service);

            checkNoOverlappingTimeSlot(request);

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

    @Transactional
    public void deleteTimeSlot(Long id) {
        ensureAdminOwnsTimeSlot(id);

        if (!timeSlotRepository.existsById(id)) {
            throw new TimeSlotException("TimeSlot with ID " + id + " does not exist");
        }
        if (bookingRepository.existsByTimeSlotId(id)) {
            throw new BookingException("Cannot delete TimeSlot with ID " + id + " because it has bookings");
        }
        timeSlotRepository.deleteById(id);
    }

    private void setTimeSlotAsUnavailable(TimeSlot timeSlot) {
        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);
    }


    //Had to use the BookingRepository directly to avoid circular dependency issues
    //This is used after a booking is made to check if the time slot is full and update its availability status
    public void checkTimeSlotAvailabilityAndUpdateIfFull(TimeSlot timeSlot) {
        if (bookingRepository.countByTimeSlotId(timeSlot.getId()) >= timeSlot.getMaxParticipants()) {
            setTimeSlotAsUnavailable(timeSlot);
        }
    }

    public void setTimeSlotAvailable(Long timeSlotId) {
        TimeSlot timeSlot = getTimeSlotById(timeSlotId);
        timeSlot.setIsAvailable(true);
        timeSlotRepository.save(timeSlot);
    }

    // Checks if the time slot is available before booking to avoid exceeding max participants
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

        // Convert LocalDateTime to OffsetDateTime as UTC
        OffsetDateTime startTime = timeSlot.getStartTime().atOffset(ZoneOffset.UTC);
        OffsetDateTime endTime = timeSlot.getEndTime().atOffset(ZoneOffset.UTC);

        return new TimeSlotResponse(timeSlot.getId(), startTime, endTime, timeSlot.getLocation(), timeSlot.getMaxParticipants(), timeSlot.getIsAvailable(), offeredServiceResponse);
    }


    public LocalDateTime calculateEndTime(Integer duration, LocalDateTime startTime) {
        return startTime.plusMinutes(duration);
    }

    public List<TimeSlotResponse> getAvailableTimeSlotsByServiceIdAndDate(Long serviceId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        List<TimeSlot> slots = timeSlotRepository.findAvailableByServiceIdAndDate(serviceId, startOfDay, endOfDay);
        return slots.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TimeSlotResponse> getAdminTimeSlots() {
        Long adminId = adminService.getCurrentAuthenticatedAdminId();
        List<TimeSlot> slots = timeSlotRepository.findByOfferedServiceAdminIdAndStartTimeAfter(adminId, LocalDateTime.now());

        return slots.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void ensureAdminOwnsTimeSlot(Long timeSlotId) {
        Long adminId = adminService.getCurrentAuthenticatedAdminId();
        TimeSlot timeSlot = getTimeSlotById(timeSlotId);

        if (!timeSlot.getOfferedService().getAdmin().getId().equals(adminId)) {
            throw new TimeSlotException("You do not have permission to access this time slot");
        }
    }
}

