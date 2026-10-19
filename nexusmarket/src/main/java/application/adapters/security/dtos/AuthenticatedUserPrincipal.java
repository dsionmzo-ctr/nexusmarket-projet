package application.adapters.security.dtos;

import application.domain.models.User;
import application.domain.valueobjects.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security principal that wraps the {@link User} Domain Model loaded for the current request.
 * Spring types stay in the adapters layer; controllers obtain the domain user with {@link #domainUser()}.
 */
public class AuthenticatedUserPrincipal implements UserDetails {

    private final User user;

    public AuthenticatedUserPrincipal(User user) {
        this.user = user;
    }

    public User domainUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user == null || user.getRole() == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user == null ? null : user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user != null && !UserStatus.BLOCKED.equals(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user != null && UserStatus.ACTIVE.equals(user.getStatus());
    }
}
