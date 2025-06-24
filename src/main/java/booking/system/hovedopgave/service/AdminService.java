package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.AdminSummaryResponse;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.repository.AdminRepository;
import booking.system.hovedopgave.security.AdminDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }



    public Admin saveAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword())); // Ensure password is set before saving
        return adminRepository.save(admin);
    }

    public Long getCurrentAuthenticatedAdminId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof AdminDetails adminDetails) {
            return adminDetails.getId(); // 👈 directly gets the admin's ID from the session
        }

        throw new RuntimeException("Could not identify authenticated admin");
    }

    public String getCurrentAuthenticatedAdminName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof AdminDetails adminDetails) {
            return adminDetails.getName();
        }

        throw new RuntimeException("Could not identify authenticated admin");
    }

    public AdminSummaryResponse toDto(Admin admin) {
        return new AdminSummaryResponse(admin.getId(), admin.getName());
    }

    public List<AdminSummaryResponse> getAllAdminSummaries() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .map(this::toDto)
                .toList();
    }
}

