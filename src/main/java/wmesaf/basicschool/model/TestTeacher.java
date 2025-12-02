package wmesaf.basicschool.model;

import java.time.LocalDate;

public class TestTeacher {
    public static void main(String[] args) {
        Teacher teacher = new Teacher(
            "Dr. Mohammed Salem",
            "mohammed@school.edu.ly",
            "0923456789",
            "Benghazi, Libya",
            LocalDate.of(1980, 7, 20),
            "TCH2025001",
            "Mathematics",
            2500.75,
            LocalDate.of(2015, 1, 10)
        );
        
        teacher.setId(100);
        
        System.out.println("=== Testing Teacher Class ===");
        System.out.println("Name: " + teacher.getName());
        System.out.println("Email: " + teacher.getEmail());
        System.out.println("Teacher ID: " + teacher.getTeacherId());
        System.out.println("Subject: " + teacher.getSubject());
        System.out.println("Salary: " + teacher.getFormattedSalary());
        System.out.println("Years of Service: " + teacher.getYearsOfService());
        System.out.println("Role: " + teacher.getRole()); // Polymorphism
        System.out.println("Complete: " + teacher);
        
        // Test Copy Constructor
        Teacher copy = new Teacher(teacher);
        copy.setTeacherId("TCH2025002");
        copy.setSubject("Physics");
        
        System.out.println("\n=== Copy Constructor Test ===");
        System.out.println("Original Subject: " + teacher.getSubject()); // Still Mathematics
        System.out.println("Copy Subject: " + copy.getSubject()); // Changed to Physics
        
        System.out.println("\n✅ Teacher class implemented successfully!");
    }
}