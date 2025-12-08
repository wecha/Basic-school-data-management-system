package wmesaf.basicschool.business;

import wmesaf.basicschool.model.Student;
import wmesaf.basicschool.model.Teacher;
import java.time.LocalDate;

/**
 * Factory Pattern implementation for creating Person objects.
 * Centralizes object creation logic and validation.
 */
public class PersonFactory {
    
    /**
     * Creates a validated Student object.
     * Implements Factory Method pattern.
     */
    public static Student createStudent(String name, String email, String phone,
                                       String address, LocalDate birthDate,
                                       String studentId, String grade, 
                                       LocalDate enrollmentDate) {
        
        // Validation logic
        validateCommonFields(name, email);
        
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade is required");
        }
        
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
        
        // Create student with validated data
        return new Student(name, email, phone, address, birthDate, 
                          studentId, grade, enrollmentDate);
    }
    
    /**
     * Creates a validated Teacher object.
     * Implements Factory Method pattern.
     */
    public static Teacher createTeacher(String name, String email, String phone,
                                       String address, LocalDate birthDate,
                                       String teacherId, String subject,
                                       double salary, LocalDate hireDate) {
        
        // Validation logic
        validateCommonFields(name, email);
        
        if (teacherId == null || teacherId.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required");
        }
        
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        
        if (hireDate == null) {
            hireDate = LocalDate.now();
        }
        
        // Create teacher with validated data
        return new Teacher(name, email, phone, address, birthDate,
                          teacherId, subject, salary, hireDate);
    }
    
    /**
     * Common validation logic reused by both factory methods.
     */
    private static void validateCommonFields(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
    }
    
    /**
     * Creates a person based on type parameter.
     * Demonstrates polymorphism in factory pattern.
     */
    public static Object createPerson(String type, String name, String email, 
                                     String phone, String address, LocalDate birthDate,
                                     String personId, String info1, 
                                     double info2, LocalDate dateInfo) {
        
        switch (type.toUpperCase()) {
            case "STUDENT":
                return createStudent(name, email, phone, address, birthDate,
                                   personId, info1, dateInfo);
            
            case "TEACHER":
                return createTeacher(name, email, phone, address, birthDate,
                                   personId, info1, info2, dateInfo);
            
            default:
                throw new IllegalArgumentException("Unknown person type: " + type);
        }
    }
}