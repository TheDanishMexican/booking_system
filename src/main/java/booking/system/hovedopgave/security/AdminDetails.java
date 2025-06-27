package booking.system.hovedopgave.security;

import booking.system.hovedopgave.model.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class AdminDetails implements UserDetails {
    private final Admin admin;

    public AdminDetails(Admin admin) {
        this.admin = admin;
    }

    // All yoga teachers in this system are treated as admins of their own dashboards.
    // Since there are no roles or permission levels beyond that, every admin is assigned the same authority.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getEmail();
    }

    public Long getId() {
        return admin.getId();
    }

    public String getName() {
        return admin.getName();
    }

    // These methods are required by the UserDetails interface to represent account status.
    // In this project, I don’t use features like account locking, expiration, or disabling,
    // so I simply return true for all of them to indicate the account is always active.
    // They must still be implemented because AdminDetails implements UserDetails.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
