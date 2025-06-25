package booking.system.hovedopgave.security;

import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// This class tells Spring Security how to look up a user (Admin) in your database during login.
@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // This method is called automatically by Spring Security when a user tries to log in.
    // It receives the "username" (in this case: email), and must return a UserDetails object.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Look up the Admin user by email
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        // Wrap the Admin object in a Spring Security-compatible UserDetails object
        return new AdminDetails(admin);
    }
}
