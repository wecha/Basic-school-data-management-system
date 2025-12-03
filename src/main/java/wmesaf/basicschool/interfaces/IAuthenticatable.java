package wmesaf.basicschool.interfaces;

/**
 * Interface defining authentication functionality for system users.
 * Any class that implements this interface can provide authentication
 * capabilities for user login and account management.
 * 
 * <p>This interface demonstrates polymorphism by allowing different
 * user types (Admin, Teacher, Student) to implement their own
 * authentication logic while being treated uniformly through this interface.</p>
 * 
 * @author wmesaf
 * @version 1.0
 */
public interface IAuthenticatable {
    
    /**
     * Authenticates a user with the provided credentials.
     * 
     * @param username the username to verify
     * @param password the password to verify
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(String username, String password);
    
    /**
     * Returns the role of the authenticated user.
     * 
     * @return the user's role as a String (e.g., "ADMIN", "TEACHER", "STUDENT")
     */
    String getRole();
    
    /**
     * Checks if the user account is currently active.
     * 
     * @return true if the account is active, false if deactivated
     */
    boolean isActive();
}