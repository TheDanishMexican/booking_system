package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    @Query("""
            SELECT t
            FROM TimeSlot t
            WHERE t.offeredService.id = :serviceId
                AND t.startTime >= :startOfDay
                AND t.startTime < :endOfDay
                AND t.isAvailable = true
                """)
    List<TimeSlot> findAvailableByServiceIdAndDate(@Param("serviceId") Long serviceId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    Boolean existsByOfferedServiceId(Long id);

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM TimeSlot t
            WHERE t.startTime < :endTime
                AND t.endTime > :startTime
                AND t.offeredService.admin.id = :adminId
                """)
    Boolean existsOverlappingTimeSlot(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("adminId") Long adminId);

    List<TimeSlot> findByOfferedServiceAdminIdAndStartTimeAfter(Long adminId, LocalDateTime now);

}
