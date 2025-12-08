package wmesaf.basicschool.business;

import wmesaf.basicschool.dao.TeacherDAO;
import wmesaf.basicschool.model.Teacher;
import java.util.List;
import java.time.LocalDate;

/**
 * Business Service for Teacher operations.
 */
public class TeacherService {
    private TeacherDAO teacherDAO;
    
    public TeacherService() {
        this.teacherDAO = new TeacherDAO();
    }
    
    public boolean addTeacher(Teacher teacher) {
        // Business validation rules
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher name is required");
        }
        
        if (teacher.getTeacherId() == null || teacher.getTeacherId().trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        
        if (teacher.getSubject() == null || teacher.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required");
        }
        
        // Check duplicate teacher ID
        if (teacherDAO.teacherIdExists(teacher.getTeacherId())) {
            throw new IllegalArgumentException("Teacher ID '" + teacher.getTeacherId() + "' already exists");
        }
        
        // Business rule: Salary cannot be negative
        if (teacher.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        
        // Business rule: Minimum salary
        if (teacher.getSalary() < 1000) {
            throw new IllegalArgumentException("Salary must be at least $1000");
        }
        
        // Business rule: Hire date cannot be in future
        if (teacher.getHireDate() != null && teacher.getHireDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }
        
        // Business rule: Minimum age for teacher (21 years)
        if (teacher.getBirthDate() != null) {
            int age = LocalDate.now().getYear() - teacher.getBirthDate().getYear();
            if (age < 21) {
                throw new IllegalArgumentException("Teacher must be at least 21 years old");
            }
        }
        
        return teacherDAO.addTeacher(teacher);
    }
    
    public List<Teacher> getAllTeachers() {
        return teacherDAO.getAllTeachers();
    }
    
    public Teacher getTeacherById(int id) {
        return teacherDAO.getTeacherById(id);
    }
    
    public Teacher getTeacherByTeacherId(String teacherId) {
        return teacherDAO.getTeacherByTeacherId(teacherId);
    }
    
    public boolean updateTeacher(Teacher teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        return teacherDAO.updateTeacher(teacher);
    }
    
    public boolean deleteTeacher(int id) {
        return teacherDAO.deleteTeacher(id);
    }
    
    public List<Teacher> searchTeachersByName(String name) {
        return teacherDAO.searchTeachersByName(name);
    }
    
    public List<Teacher> getTeachersBySubject(String subject) {
        return teacherDAO.getTeachersBySubject(subject);
    }
    
    public int countTeachers() {
        return teacherDAO.countTeachers();
    }
    
    public boolean teacherIdExists(String teacherId) {
        return teacherDAO.teacherIdExists(teacherId);
    }
    
    // Business logic: Calculate total salary expense
    public double calculateTotalSalaryExpense() {
        List<Teacher> teachers = getAllTeachers();
        return teachers.stream()
            .mapToDouble(Teacher::getSalary)
            .sum();
    }
    
    // Business logic: Calculate average salary
    public double calculateAverageSalary() {
        List<Teacher> teachers = getAllTeachers();
        if (teachers.isEmpty()) return 0;
        
        return teachers.stream()
            .mapToDouble(Teacher::getSalary)
            .average()
            .orElse(0);
    }
    
    // Business logic: Get teachers by experience
    public List<Teacher> getTeachersByExperience(int minYears, int maxYears) {
        List<Teacher> allTeachers = getAllTeachers();
        return allTeachers.stream()
            .filter(teacher -> {
                if (teacher.getHireDate() == null) return false;
                int experience = LocalDate.now().getYear() - teacher.getHireDate().getYear();
                return experience >= minYears && experience <= maxYears;
            })
            .toList();
    }
}