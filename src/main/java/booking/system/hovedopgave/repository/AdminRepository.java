package booking.system.hovedopgave.repository;

import booking.system.hovedopgave.dto.AdminSummaryRequest;
import booking.system.hovedopgave.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    @Query("SELECT new booking.system.hovedopgave.dto.AdminSummaryRequest(a.id, a.name) FROM Admin a")
    List<AdminSummaryRequest> findAllAdminSummaries();
}
