package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Long countByTimeSlotId(Long timeSlotId);

    Boolean existsByEmailAndTimeSlotId(String email, Long id);

    Boolean existsByTimeSlotId(Long timeSlotId);

    @Query("SELECT b FROM Booking b WHERE b.timeSlot.offeredService.admin.id = :adminId")
    List<Booking> findAllByAdminId(@Param("adminId") Long adminId);

}
