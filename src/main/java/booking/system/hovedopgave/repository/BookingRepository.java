package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByTimeSlotId(Long timeSlotId);
    Long countByTimeSlotId(Long timeSlotId);
}
