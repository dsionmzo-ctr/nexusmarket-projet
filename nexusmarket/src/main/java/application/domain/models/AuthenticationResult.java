package application.domain.models;

/**
 * Outcome of a successful login: the authenticated user, the issued token and its lifetime.
 */
public class AuthenticationResult {

    private User authenticatedUser;
    private String token;
    private long expiresIn;

    public AuthenticationResult() {
    }

    public AuthenticationResult(User authenticatedUser, String token, long expiresIn) {
        this.authenticatedUser = authenticatedUser;
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

}
