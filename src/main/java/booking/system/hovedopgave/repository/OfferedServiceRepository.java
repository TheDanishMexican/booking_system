package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.model.OfferedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferedServiceRepository extends JpaRepository<OfferedService, Long> {
    boolean existsByName(String name);
    List<OfferedService> findByAdminId(Long adminId);
}
