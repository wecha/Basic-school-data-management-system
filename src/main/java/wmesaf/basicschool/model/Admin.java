package wmesaf.basicschool.model;

import wmesaf.basicschool.interfaces.IAuthenticatable;

/**
 * Represents an administrator in the school management system.
 * This class implements the IAuthenticatable interface to provide
 * authentication functionality for system administrators.
 * 
 * <p>Administrators have full access to manage students, teachers,
 * and system settings. Each admin has a unique username and password
 * for authentication.</p>
 * 
 * <p>This class demonstrates:
 * <ul>
 *   <li>Interface implementation (IAuthenticatable)</li>
 *   <li>Encapsulation through private fields with public getters/setters</li>
 *   <li>Exception handling for invalid data</li>
 *   <li>Polymorphism through interface methods</li>
 * </ul>
 * </p>
 * 
 * @author wmesaf
 * @version 1.0
 * @see IAuthenticatable
 * @see Person
 */
public class Admin implements IAuthenticatable {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private boolean active;
    
    /**
     * Constructs a new Admin with the specified credentials.
     * The admin account will be active by default.
     * 
     * @param username the administrator's username (must not be empty)
     * @param password the administrator's password (must be at least 6 characters)
     * @param fullName the administrator's full name
     * @param email the administrator's email address
     * @throws IllegalArgumentException if username is empty or password is too short
     */
    public Admin(String username, String password, String fullName, String email) {
        setUsername(username);
        setPassword(password);
        setFullName(fullName);
        setEmail(email);
        this.active = true;
    }
    
    /**
     * Copy constructor that creates a deep copy of an existing Admin object.
     * 
     * @param original the Admin object to copy
     * @throws NullPointerException if original is null
     */
    public Admin(Admin original) {
        if (original == null) {
            throw new NullPointerException("Original Admin cannot be null");
        }
        
        this.id = original.id;
        this.username = original.username;
        this.password = original.password;
        this.fullName = original.fullName;
        this.email = original.email;
        this.active = original.active;
    }
    
    /**
     * Authenticates the admin using provided username and password.
     * Implementation of the IAuthenticatable interface method.
     * 
     * @param username the username to verify
     * @param password the password to verify
     * @return true if both username and password match, false otherwise
     */
    @Override
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
    
    /**
     * Returns the role of this user.
     * Implementation of the IAuthenticatable interface method.
     * 
     * @return the string "ADMIN" representing the administrator role
     */
    @Override
    public String getRole() {
        return "ADMIN";
    }
    
    /**
     * Checks if the admin account is active.
     * Implementation of the IAuthenticatable interface method.
     * 
     * @return true if the account is active, false otherwise
     */
    @Override
    public boolean isActive() {
        return active;
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    /**
     * Returns the unique identifier of the admin.
     * 
     * @return the admin ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the admin.
     * 
     * @param id the new ID to set
     * @throws IllegalArgumentException if id is negative
     */
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = id;
    }

    /**
     * Returns the admin's username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the admin's username.
     * 
     * @param username the new username
     * @throws IllegalArgumentException if username is null or empty
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username.trim();
    }

    /**
     * Returns the admin's password.
     * Note: In a production system, passwords should be hashed.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the admin's password.
     * 
     * @param password the new password
     * @throws IllegalArgumentException if password is null or less than 6 characters
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        this.password = password;
    }

    /**
     * Returns the admin's full name.
     * 
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the admin's full name.
     * 
     * @param fullName the new full name
     * @throws IllegalArgumentException if fullName is null or empty
     */
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }
        this.fullName = fullName.trim();
    }

    /**
     * Returns the admin's email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the admin's email address.
     * 
     * @param email the new email address
     * @throws IllegalArgumentException if email is null, empty, or invalid format
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim().toLowerCase();
    }
    
    /**
     * Sets the active status of the admin account.
     * 
     * @param active the new active status
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    // ==================== ADDITIONAL METHODS ====================
    
    /**
     * Changes the admin's password after verifying the old password.
     * 
     * @param oldPassword the current password for verification
     * @param newPassword the new password to set
     * @return true if password was changed successfully
     * @throws IllegalArgumentException if old password is incorrect
     * @throws IllegalArgumentException if new password is invalid
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password must be different from old password");
        }
        setPassword(newPassword);
        return true;
    }
    
    /**
     * Activates the admin account.
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * Deactivates the admin account.
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Returns a string representation of the Admin object.
     * Password is excluded for security reasons.
     * 
     * @return a string containing admin details
     */
    @Override
    public String toString() {
        return "Admin{" + 
               "id=" + id + 
               ", username='" + username + '\'' + 
               ", fullName='" + fullName + '\'' + 
               ", email='" + email + '\'' + 
               ", active=" + active + 
               ", role='" + getRole() + '\'' +
               '}';
    }
    
    /**
     * Returns a safe string representation without sensitive information.
     * Suitable for logging and display purposes.
     * 
     * @return a safe string representation
     */
    public String toSafeString() {
        return "Admin: " + fullName + " (" + username + ") - " + getRole();
    }
}