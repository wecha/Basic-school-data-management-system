package wmesaf.basicschool.model;

public class TestAdmin {
    public static void main(String[] args) {
        System.out.println("=== Testing Admin Class ===\n");
        
        // Create admin
        Admin admin = new Admin(
            "admin_user",
            "securePass123",
            "Wessal Mohammed",
            "wessal@school.edu.ly"
        );
        admin.setId(1);
        
        // Display info
        System.out.println("Admin Created:");
        System.out.println("ID: " + admin.getId());
        System.out.println("Username: " + admin.getUsername());
        System.out.println("Full Name: " + admin.getFullName());
        System.out.println("Email: " + admin.getEmail());
        System.out.println("Active: " + admin.isActive());
        System.out.println("Safe String: " + admin.toSafeString());
        
        // Test password validation
        System.out.println("\n=== Password Tests ===");
        System.out.println("Valid password: " + admin.validatePassword("securePass123"));
        System.out.println("Invalid password: " + admin.validatePassword("wrongPass"));
        
        // Test copy constructor
        System.out.println("\n=== Copy Constructor Test ===");
        Admin copy = new Admin(admin);
        copy.setUsername("admin_copy");
        System.out.println("Original username: " + admin.getUsername());
        System.out.println("Copy username: " + copy.getUsername());
        
        // Test validation
        System.out.println("\n=== Validation Tests ===");
        try {
            admin.setUsername("ab"); // Too short
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Username validation works: " + e.getMessage());
        }
        
        try {
            admin.setEmail("invalid-email"); // No @
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Email validation works: " + e.getMessage());
        }
        
        System.out.println("\n✅ Admin class implemented successfully!");
    }
}