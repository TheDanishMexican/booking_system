package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.dto.TimeSlotResponse;
import booking.system.hovedopgave.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    @Query("SELECT t FROM TimeSlot t WHERE t.offeredService.id = :serviceId AND t.isAvailable = true AND t.startTime > CURRENT_TIMESTAMP")
    List<TimeSlot> findAllAvailableAndFutureByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT t FROM TimeSlot t WHERE t.offeredService.id = :serviceId AND t.startTime >= :startOfDay AND t.startTime < :endOfDay AND t.isAvailable = true")
    List<TimeSlot> findAvailableByServiceIdAndDate(Long serviceId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<TimeSlot> findByStartTimeAfter(LocalDateTime now);

    List<TimeSlot> findByStartTimeBefore(LocalDateTime now);

    Boolean existsByOfferedServiceId(Long id);

    @Query("""
SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
    FROM TimeSlot t
    WHERE t.startTime < :endTime
      AND t.endTime > :startTime
""")
    boolean existsOverlappingTimeSlot(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    List<TimeSlot> findByOfferedServiceAdminIdAndStartTimeAfter(Long adminId, LocalDateTime now);

}
