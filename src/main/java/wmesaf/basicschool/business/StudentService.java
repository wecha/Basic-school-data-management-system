package wmesaf.basicschool.business;

import wmesaf.basicschool.dao.StudentDAO;
import wmesaf.basicschool.model.Student;
import java.util.List;
import java.time.LocalDate;

/**
 * Business Service for Student operations.
 * Implements business logic layer between presentation and data access.
 */
public class StudentService {
    private StudentDAO studentDAO;
    
    public StudentService() {
        this.studentDAO = new StudentDAO();
    }
    
    public boolean addStudent(Student student) {
        // Business validation rules
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name is required");
        }
        
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        
        // Check duplicate student ID
        if (studentDAO.studentIdExists(student.getStudentId())) {
            throw new IllegalArgumentException("Student ID '" + student.getStudentId() + "' already exists");
        }
        
        // Business rule: Birth date cannot be in future
        if (student.getBirthDate() != null && student.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        
        // Business rule: Enrollment date cannot be in future
        if (student.getEnrollmentDate() != null && student.getEnrollmentDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Enrollment date cannot be in the future");
        }
        
        // Business rule: Minimum age check (optional)
        if (student.getBirthDate() != null) {
            int age = LocalDate.now().getYear() - student.getBirthDate().getYear();
            if (age < 5) {
                throw new IllegalArgumentException("Student must be at least 5 years old");
            }
        }
        
        // All validations passed, save to database
        return studentDAO.addStudent(student);
    }
    
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }
    
    public Student getStudentById(int id) {
        return studentDAO.getStudentById(id);
    }
    
    public Student getStudentByStudentId(String studentId) {
        return studentDAO.getStudentByStudentId(studentId);
    }
    
    public boolean updateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        return studentDAO.updateStudent(student);
    }
    
    public boolean deleteStudent(int id) {
        return studentDAO.deleteStudent(id);
    }
    
    public List<Student> searchStudentsByName(String name) {
        return studentDAO.searchStudentsByName(name);
    }
    
    public List<Student> getStudentsByGrade(String grade) {
        return studentDAO.getStudentsByGrade(grade);
    }
    
    public int countStudents() {
        return studentDAO.countStudents();
    }
    
    public boolean studentIdExists(String studentId) {
        return studentDAO.studentIdExists(studentId);
    }
    
    // ✅ الدالة المفقودة: Calculate average age of students
    public double calculateAverageAge() {
        List<Student> students = getAllStudents();
        if (students.isEmpty()) return 0;
        
        int totalAge = 0;
        int count = 0;
        
        for (Student student : students) {
            if (student.getBirthDate() != null) {
                int age = LocalDate.now().getYear() - student.getBirthDate().getYear();
                totalAge += age;
                count++;
            }
        }
        
        return count > 0 ? (double) totalAge / count : 0;
    }
    
    // ✅ دالة إضافية: Get students by age range
    public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
        List<Student> allStudents = getAllStudents();
        return allStudents.stream()
            .filter(student -> {
                if (student.getBirthDate() == null) return false;
                int age = LocalDate.now().getYear() - student.getBirthDate().getYear();
                return age >= minAge && age <= maxAge;
            })
            .toList();
    }
    
    // ✅ دالة إضافية: Get grade distribution
    public java.util.Map<String, Integer> getGradeDistribution() {
        List<Student> students = getAllStudents();
        java.util.Map<String, Integer> distribution = new java.util.HashMap<>();
        
        for (Student student : students) {
            String grade = student.getGrade();
            distribution.put(grade, distribution.getOrDefault(grade, 0) + 1);
        }
        
        return distribution;
    }
    
    // ✅ دالة إضافية: Get recent students (last 30 days)
    public int getRecentStudentsCount() {
        // This is a simplified version. In real app, you'd query by date.
        List<Student> students = getAllStudents();
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        
        int count = 0;
        for (Student student : students) {
            // Assuming enrollmentDate is when student was added
            if (student.getEnrollmentDate() != null && 
                student.getEnrollmentDate().isAfter(thirtyDaysAgo)) {
                count++;
            }
        }
        
        return count;
    }
}