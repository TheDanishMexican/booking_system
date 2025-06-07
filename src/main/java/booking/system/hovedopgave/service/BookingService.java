package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.BookingRequest;
import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TimeSlotService timeSlotService;
    @Autowired
    private CustomerService customerService;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        try {
            TimeSlot slot = timeSlotService.getTimeSlotById(request.timeSlotId());


            Booking booking = new Booking();
            booking.setEmail(request.email());
            booking.setName(request.name());
            booking.setPhone(request.phone());
            booking.setTimeSlot(slot);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaid(true);
            booking = bookingRepository.save(booking);

            if (bookingRepository.countByTimeSlotId(slot.getId()) >= slot.getMaxParticipants()) {
                timeSlotService.markAsUnavailableIfFull(slot);
            }

            return booking;

        } catch (Exception e) {
            throw new RuntimeException("Booking failed: " + e.getMessage());
        }
    }



    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}

