package wmesaf.basicschool.dao;

import wmesaf.basicschool.model.Admin;
import wmesaf.basicschool.model.PersonRecord;
import wmesaf.basicschool.model.Student;
import wmesaf.basicschool.model.Teacher;
import java.time.LocalDate;

public class TestDAO {
    public static void main(String[] args) {
        System.out.println("=== Testing All DAO Classes ===\n");
        
        // Test AdminDAO
        System.out.println("1. Testing AdminDAO:");
        AdminDAO adminDAO = new AdminDAO();
        
        Admin admin = adminDAO.authenticate("admin", "admin123");
        if (admin != null) {
            System.out.println("   ✅ Admin authenticated: " + admin.getFullName());
        } else {
            System.out.println("   ⚠️ Admin not found, adding default...");
            adminDAO.addAdmin(new Admin("admin", "admin123", "System Admin", "admin@school.com"));
        }
        
        // Test PersonDAO with PersonRecord
        System.out.println("\n2. Testing PersonDAO with PersonRecord:");
        PersonDAO personDAO = new PersonDAO();
        
        System.out.println("   Total persons in database: " + personDAO.countPersons());
        
        // Print all persons
        System.out.println("\n   All Persons:");
        for (PersonRecord person : personDAO.getAllPersons()) {
            System.out.println("     " + person.toDisplayString());
        }
        
        // Test StudentDAO
        System.out.println("\n3. Testing StudentDAO:");
        StudentDAO studentDAO = new StudentDAO();
        
        int studentCount = studentDAO.countStudents();
        System.out.println("   Total students: " + studentCount);
        
        if (studentCount == 0) {
            System.out.println("   Adding sample student...");
            Student sampleStudent = new Student(
                "Ahmed Student",
                "ahmed@school.com",
                "0912345678",
                "Tripoli, Libya",
                LocalDate.of(2005, 3, 15),
                "STU001",
                "10th Grade",
                LocalDate.of(2023, 9, 1)
            );
            if (studentDAO.addStudent(sampleStudent)) {
                System.out.println("   ✅ Sample student added");
            }
        }
        
        // Test TeacherDAO
        System.out.println("\n4. Testing TeacherDAO:");
        TeacherDAO teacherDAO = new TeacherDAO();
        
        int teacherCount = teacherDAO.countTeachers();
        System.out.println("   Total teachers: " + teacherCount);
        
        if (teacherCount == 0) {
            System.out.println("   Adding sample teacher...");
            Teacher sampleTeacher = new Teacher(
                "Dr. Sarah Teacher",
                "sarah@school.com",
                "0923456789",
                "Benghazi, Libya",
                LocalDate.of(1980, 7, 20),
                "TCH001",
                "Mathematics",
                2500.00,
                LocalDate.of(2018, 8, 15)
            );
            if (teacherDAO.addTeacher(sampleTeacher)) {
                System.out.println("   ✅ Sample teacher added");
            }
        }
        
        // Print final statistics
        System.out.println("\n5. Final Database Statistics:");
        System.out.println("   Persons: " + personDAO.countPersons());
        System.out.println("   Students: " + studentDAO.countStudents());
        System.out.println("   Teachers: " + teacherDAO.countTeachers());
        
        System.out.println("\n=== All DAO Tests Completed Successfully ===");
    }
}