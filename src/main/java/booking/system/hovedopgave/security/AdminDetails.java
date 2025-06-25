package booking.system.hovedopgave.security;

import booking.system.hovedopgave.model.Admin;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// This class adapts your Admin model to work with Spring Security.
// It implements UserDetails, which is the interface Spring Security uses to represent users.

public class AdminDetails implements UserDetails {
    private final Admin admin;

    // Constructor: wraps an Admin object
    public AdminDetails(Admin admin) {
        this.admin = admin;
    }

    // Returns the roles/authorities assigned to the user.
    // In this case, we assign every admin the role "ROLE_ADMIN".
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    // Returns the user's password (hashed in the database).
    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    // Returns the user's login identifier (email in this case).
    @Override
    public String getUsername() {
        return admin.getEmail();
    }

    public Long getId() {
        return admin.getId();
    }

    // The following four methods define account status flags.
    // For now, we just return true for all of them.

    // Account is not expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Account is not locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Credentials (password) are not expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Account is enabled and usable
    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return admin.getName(); // Returns the name of the admin
    }
}
