package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.BookingRequest;
import booking.system.hovedopgave.dto.BookingResponse;
import booking.system.hovedopgave.exception.BookingException;
import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotService timeSlotService;

    public BookingService(BookingRepository bookingRepository, TimeSlotService timeSlotService) {
        this.bookingRepository = bookingRepository;
        this.timeSlotService = timeSlotService;
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
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaid(true);
            booking = bookingRepository.save(booking);

            timeSlotService.setTimeSlotUnavailableIfFull(timeSlot);

            return toDto(booking);

        } catch (Exception e) {
            throw new BookingException("Booking failed: " + e.getMessage(), e);
        }
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingException("Booking with ID " + id + " does not exist");
        }
        bookingRepository.deleteById(id);
    }

    public void validateBooking(BookingRequest request) {
        if (bookingRepository.existsByEmailAndTimeSlotId(request.email(), request.timeSlotId())) {
            throw new BookingException("A booking with this email and time slot already exists");
        }
    }

    public BookingResponse toDto(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getName(),
                booking.getEmail(),
                booking.getPhone(),
                booking.getTimeSlot().getId(),
                booking.getStatus().name(),
                booking.isPaid()
        );
    }
}

