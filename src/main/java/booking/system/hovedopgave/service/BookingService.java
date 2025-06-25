package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.BookingRequest;
import booking.system.hovedopgave.dto.BookingResponse;
import booking.system.hovedopgave.dto.TimeSlotResponse;
import booking.system.hovedopgave.exception.BookingException;
import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotService timeSlotService;
    private final AdminService adminService;

    public BookingService(BookingRepository bookingRepository, TimeSlotService timeSlotService, AdminService adminService) {
        this.bookingRepository = bookingRepository;
        this.timeSlotService = timeSlotService;
        this.adminService = adminService;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        try {
            TimeSlot timeSlot = timeSlotService.getTimeSlotById(request.timeSlotId());

            validateBooking(request);

            timeSlotService.checkTimeSlotAvailability(timeSlot);

            Booking booking = new Booking();
            booking.setEmail(request.email());
            booking.setName(request.name());
            booking.setPhone(request.phone());
            booking.setTimeSlot(timeSlot);
            booking.setStatus(BookingStatus.PENDING);
            booking.setPaid(true);
            booking = bookingRepository.save(booking);

            timeSlotService.setTimeSlotUnavailableIfFull(timeSlot);

            return toDto(booking);

        } catch (Exception e) {
            throw new BookingException("Booking failed: " + e.getMessage(), e);
        }
    }

    public List<BookingResponse> getAllBookingsForCurrentAdmin() {
        Long adminId = adminService.getCurrentAuthenticatedAdminId();
        List<Booking> bookings = bookingRepository.findAllByAdminId(adminId);
        return bookings.stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deleteBooking(Long id) {
        ensureAdminOwnsBooking(id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("Booking with ID " + id + " does not exist"));
        timeSlotService.setTimeSlotAvailable(booking.getTimeSlot().getId());
        bookingRepository.deleteById(id);
    }

    public void validateBooking(BookingRequest request) {
        if (bookingRepository.existsByEmailAndTimeSlotId(request.email(), request.timeSlotId())) {
            throw new BookingException("A booking with this email and time slot already exists");
        }
    }

    private void ensureAdminOwnsBooking(Long bookingId) {
        Long adminId = adminService.getCurrentAuthenticatedAdminId();
        Booking booking = getBookingById(bookingId);

        if (!booking.getTimeSlot().getOfferedService().getAdmin().getId().equals(adminId)) {
            throw new BookingException("You do not have permission to access this booking");
        }
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking with ID " + bookingId + " does not exist"));
    }

    public BookingResponse toDto(Booking booking) {
       TimeSlotResponse timeSlotResponse = timeSlotService.toDto(booking.getTimeSlot());

        return new BookingResponse(
                booking.getId(),
                booking.getName(),
                booking.getEmail(),
                booking.getPhone(),
                timeSlotResponse,
                booking.getStatus().name(),
                booking.isPaid()
        );
    }
}

