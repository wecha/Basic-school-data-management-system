package wmesaf.basicschool.model;

import java.time.LocalDate;

public class TestStudent {
    public static void main(String[] args) {
        // إنشاء طالب
        Student student1 = new Student(
            "Adel kotceri",                    // name
            "Adel@school.edu.ly",          // email
            "0912345678",                   // phone
            "albta, Libya",               // address
            LocalDate.of(2000, 3, 15),      // birthDate
            "2025001",                   // studentId
            "Grade 1",                     // grade
            LocalDate.of(2018, 9, 1)        // enrollmentDate
        );
        
        student1.setId(1);
        
        // عرض المعلومات
        System.out.println("=== Testing Student Class ===");
        System.out.println("Name: " + student1.getName());
        System.out.println("Email: " + student1.getEmail());
        System.out.println("Student ID: " + student1.getStudentId());
        System.out.println("Grade: " + student1.getGrade());
        System.out.println("Role: " + student1.getRole()); // Polymorphism
        System.out.println("Complete Info: " + student1);
        
        // اختبار Copy Constructor
        System.out.println("\n=== Testing Copy Constructor ===");
        Student student2 = new Student(student1);
        student2.setStudentId("2025002");
        student2.setName("Mohammed Ahmed");
        
        System.out.println("Original: " + student1.getName());
        System.out.println("Copy: " + student2.getName());
        System.out.println("Copy Student ID: " + student2.getStudentId());
        
        System.out.println("\n Student class works correctly!");
    }
}