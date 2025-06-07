package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findAllByOfferedService_IdAndIsAvailableTrue(Long serviceId);

}
