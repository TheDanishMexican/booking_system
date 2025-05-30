package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.model.OfferedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferedServiceRepository extends JpaRepository<OfferedService, Long> {
}
