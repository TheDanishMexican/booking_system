package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.BookingRequest;
import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.BookingRepository;
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


    public Booking createBooking(BookingRequest bookingRequest) {
        TimeSlot timeSlot = timeSlotService
                .getTimeSlotById(bookingRequest.timeSlotId());

        Customer customer = customerService
                .findByEmail(bookingRequest.customerEmail());

        Booking booking = new Booking();

        booking.setTimeSlot(timeSlot);
        booking.setCustomer(customer);
        booking.setPaid(true);
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}

