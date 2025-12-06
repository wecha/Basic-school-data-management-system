package wmesaf.basicschool.dao;

import wmesaf.basicschool.model.Student;
import java.time.LocalDate;

public class TestDAOFixed {
    public static void main(String[] args) {
        System.out.println("=== TESTING FIXED DAO ===");
        
        StudentDAO studentDAO = new StudentDAO();
        
        // 1. Test counting
        System.out.println("\n1. Current student count: " + studentDAO.countStudents());
        
        // 2. Test getting all students
        System.out.println("\n2. All students:");
        var students = studentDAO.getAllStudents();
        for (Student s : students) {
            System.out.println("   - " + s.getStudentId() + ": " + s.getName());
        }
        
        // 3. Test adding a test student
        System.out.println("\n3. Adding test student...");
        Student testStudent = new Student(
            "Test Student",
            "test@school.com",
            "0911111111",
            "Test Address",
            LocalDate.of(2006, 1, 1),
            "TEST001",
            "Test Grade",
            LocalDate.now()
        );
        
        if (studentDAO.addStudent(testStudent)) {
            System.out.println("   ✅ Test student added successfully!");
        } else {
            System.out.println("   ❌ Failed to add test student");
        }
        
        // 4. Test searching
        System.out.println("\n4. Searching for 'Test':");
        var found = studentDAO.searchStudentsByName("Test");
        System.out.println("   Found " + found.size() + " students");
        
        // 5. Test getting by student ID
        System.out.println("\n5. Getting student by ID 'TEST001':");
        var student = studentDAO.getStudentByStudentId("TEST001");
        if (student != null) {
            System.out.println("   ✅ Found: " + student.getName());
            
            // 6. Test deleting
            System.out.println("\n6. Deleting test student...");
            if (studentDAO.deleteStudent(student.getId())) {
                System.out.println("   ✅ Test student deleted successfully!");
            } else {
                System.out.println("   ❌ Failed to delete test student");
            }
        } else {
            System.out.println("   ❌ Student not found!");
        }
        
        System.out.println("\n=== TEST COMPLETE ===");
    }
}