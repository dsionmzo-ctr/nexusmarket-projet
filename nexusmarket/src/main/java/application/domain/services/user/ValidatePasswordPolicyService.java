package application.domain.services.user;

import application.domain.exceptions.InvalidUserException;

/**
 * Validates the password policy: 8 to 100 characters with at least one letter and one digit.
 */
public class ValidatePasswordPolicyService {


    public void execute(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8 || rawPassword.length() > 100) {
            throw new InvalidUserException("Password must have between 8 and 100 characters.");
        }
        boolean hasLetter = rawPassword.chars().anyMatch(Character::isLetter);
        boolean hasDigit = rawPassword.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new InvalidUserException("Password must contain at least one letter and one digit.");
        }
    }
}
