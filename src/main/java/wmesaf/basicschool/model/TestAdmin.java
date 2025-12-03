package wmesaf.basicschool.model;

public class TestAdmin {
    public static void main(String[] args) {
        System.out.println("=== Testing Admin Class ===");
        
        // Create admin
        Admin admin = new Admin("admin1", "password123", "John Doe", "admin@school.com");
        System.out.println("Admin created: " + admin.toString());
        
        // Test authentication
        System.out.println("\nAuthentication tests:");
        System.out.println("Correct login: " + admin.authenticate("admin1", "password123"));
        System.out.println("Wrong login: " + admin.authenticate("admin1", "wrong"));
        
        // Test role
        System.out.println("\nRole: " + admin.getRole());
        
        // Test active status
        System.out.println("\nActive status:");
        System.out.println("Is active: " + admin.isActive());
        admin.deactivate();
        System.out.println("After deactivate: " + admin.isActive());
        admin.activate();
        System.out.println("After activate: " + admin.isActive());
        
        System.out.println("\n=== Test completed successfully ===");
    }
}