package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.AdminRequest;
import booking.system.hovedopgave.dto.AdminSummaryResponse;
import booking.system.hovedopgave.exception.AdminException;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.repository.AdminRepository;
import booking.system.hovedopgave.security.AdminDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new AdminException("Admin not found"));
    }


    @Transactional
    public AdminSummaryResponse createAdmin(AdminRequest request) {
        try {
            Admin admin = new Admin();
            admin.setEmail(request.email());
            admin.setName(request.name());
            admin.setPassword(passwordEncoder.encode(request.password()));
            admin = adminRepository.save(admin);

            return toDto(admin);

        } catch (Exception e) {
            throw new AdminException("Admin creation failed: " + e.getMessage(), e);
        }
    }

    private AdminDetails getAuthenticatedAdminDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AdminDetails adminDetails) {
            return adminDetails;
        }
        throw new AdminException("Could not identify authenticated admin");
    }

    public Long getCurrentAuthenticatedAdminId() {
        AdminDetails adminDetails = getAuthenticatedAdminDetails();
        return adminDetails.getId();
    }

    public String getCurrentAuthenticatedAdminName() {
        AdminDetails adminDetails = getAuthenticatedAdminDetails();
        return adminDetails.getName();
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

