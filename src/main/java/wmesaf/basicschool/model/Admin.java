package wmesaf.basicschool.model;

/**
 * Represents an administrator of the school management system.
 * The admin is responsible for managing students, teachers, and system operations.
 * This class does not extend Person as admins are system users, not school personnel.
 * Demonstrates encapsulation and data validation.
 * 
 * @author wmesaf
 * @version 1.0
 */
public class Admin {
    private int id;
    private String username;
    private String password; // In real application, store hashed password
    private String fullName;
    private String email;
    private boolean isActive;
    
    /**
     * Constructs a new Admin with the specified credentials.
     * 
     * @param username the admin's username for login
     * @param password the admin's password (will be hashed in production)
     * @param fullName the admin's full name
     * @param email the admin's email address
     */
    public Admin(String username, String password, String fullName, String email) {
        setUsername(username);
        setPassword(password);
        setFullName(fullName);
        setEmail(email);
        this.isActive = true;
    }
    
    /**
     * Copy constructor for Admin.
     * Creates a deep copy of an existing Admin object.
     * 
     * @param original the Admin to copy
     */
    public Admin(Admin original) {
        this.id = original.id;
        this.username = original.username;
        this.password = original.password;
        this.fullName = original.fullName;
        this.email = original.email;
        this.isActive = original.isActive;
    }
    
    // Getters and Setters with validation
    
    /**
     * Returns the admin's unique identifier.
     * 
     * @return the admin ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the admin's unique identifier.
     * 
     * @param id the new ID
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
     * @throws IllegalArgumentException if username is null, empty, or too short
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        this.username = username.trim();
    }

    /**
     * Returns the admin's password (hashed in production).
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
     * @throws IllegalArgumentException if password is null, empty, or too weak
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        // In production, hash the password before storing
        this.password = password;
    }
    
    /**
     * Validates if the provided password matches the admin's password.
     * In production, compare hashed values.
     * 
     * @param inputPassword the password to validate
     * @return true if password matches, false otherwise
     */
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    /**
     * Changes the admin's password with validation.
     ** @param oldPassword the current password
     * @param newPassword the new password
     * @return true if password changed successfully
     * @throws IllegalArgumentException if old password is incorrect or new password is invalid
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!validatePassword(oldPassword)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        setPassword(newPassword);
        return true;
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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the admin's email address.
     * 
     * @param email the new email
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
     * Returns whether the admin account is active.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the admin account's active status.
     * 
     * @param active the new active status
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    /**
     * Activates the admin account.
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * Deactivates the admin account.
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * Returns a string representation of the Admin object.
     * Excludes password for security.
     * 
     * @return a string representation
     */
    @Override
    public String toString() {
        return "Admin{" + 
               "id=" + id + 
               ", username='" + username + '\'' + 
               ", fullName='" + fullName + '\'' + 
               ", email='" + email + '\'' + 
               ", isActive=" + isActive + 
               '}';
    }
    
    /**
     * Returns a safe representation without sensitive data.
     * 
     * @return safe string representation
     */
    public String toSafeString() {
        return "Admin: " + fullName + " (" + username + ")";
    }
}