package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    @Query("SELECT t FROM TimeSlot t WHERE t.offeredService.id = :serviceId AND t.isAvailable = true AND t.startTime > CURRENT_TIMESTAMP")
    List<TimeSlot> findAllAvailableAndFutureByServiceId(@Param("serviceId") Long serviceId);


}
