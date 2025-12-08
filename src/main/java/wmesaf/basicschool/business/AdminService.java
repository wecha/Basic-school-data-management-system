package wmesaf.basicschool.business;

import wmesaf.basicschool.dao.AdminDAO;
import wmesaf.basicschool.model.Admin;

/**
 * Business Service for Admin operations.
 */
public class AdminService {
    private AdminDAO adminDAO;
    
    public AdminService() {
        this.adminDAO = new AdminDAO();
    }
    
    public Admin authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        return adminDAO.authenticate(username, password);
    }
    
    public boolean addAdmin(Admin admin) {
        // Business validation rules
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        
        if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        if (admin.getFullName() == null || admin.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        
        if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        // Business rule: Password strength
        if (admin.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        // Business rule: Email format
        if (!admin.getEmail().contains("@") || !admin.getEmail().contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        return adminDAO.addAdmin(admin);
    }
    
    public Admin getAdminById(int id) {
        return adminDAO.getAdminById(id);
    }
    
    public java.util.List<Admin> getAllAdmins() {
        return adminDAO.getAllAdmins();
    }
}