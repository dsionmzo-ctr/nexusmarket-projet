package application.adapters.security;

import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Output adapter for password hashing and verification (BCrypt through Spring's PasswordEncoder). */
@Component
public class BCryptPasswordServiceAdapter implements PasswordServicePort {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordServiceAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(User user) {
        if (user == null || user.getRawPassword() == null || user.getPasswordHash() == null) {
            return false;
        }
        return passwordEncoder.matches(user.getRawPassword(), user.getPasswordHash());
    }

    @Override
    public String encrypt(User user) {
        return passwordEncoder.encode(user.getRawPassword());
    }
}
