package wmesaf.basicschool.business;

import wmesaf.basicschool.model.Student;
import wmesaf.basicschool.model.Teacher;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating various system reports.
 */
public class ReportService {
    private StudentService studentService;
    private TeacherService teacherService;
    
    public ReportService() {
        this.studentService = new StudentService();
        this.teacherService = new TeacherService();
    }
    
    /**
     * ✅ الدالة المفقودة: Student Statistics Report
     */
    public String generateStudentStatisticsReport() {
        List<Student> students = studentService.getAllStudents();
        
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║                 STUDENT STATISTICS REPORT                   ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");
        
        // Header
        report.append("📅 Report Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        report.append("📊 Total Students: ").append(students.size()).append("\n\n");
        
        // Grade Distribution
        if (!students.isEmpty()) {
            Map<String, Long> gradeDistribution = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getGrade,
                    Collectors.counting()
                ));
            
            report.append("📚 GRADE DISTRIBUTION\n");
            report.append("────────────────────────────────────────────────────────────\n");
            
            gradeDistribution.forEach((grade, count) -> {
                double percentage = students.size() > 0 ? (count * 100.0) / students.size() : 0;
                report.append(String.format("  %-12s: %3d students (%5.1f%%)\n", grade, count, percentage));
            });
        }
        
        report.append("\n" + "═".repeat(64) + "\n");
        report.append("End of Student Statistics Report\n");
        report.append("═".repeat(64));
        
        return report.toString();
    }
    
    /**
     * ✅ الدالة المفقودة: Teacher Statistics Report
     */
    public String generateTeacherStatisticsReport() {
        List<Teacher> teachers = teacherService.getAllTeachers();
        
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║              TEACHER STATISTICS REPORT                     ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");
        
        // Header
        report.append("📅 Report Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        report.append("👩‍🏫 Total Teachers: ").append(teachers.size()).append("\n\n");
        
        // Subject Distribution
        if (!teachers.isEmpty()) {
            Map<String, Long> subjectDistribution = teachers.stream()
                .collect(Collectors.groupingBy(
                    Teacher::getSubject,
                    Collectors.counting()
                ));
            
            report.append("📖 SUBJECT DISTRIBUTION\n");
            report.append("────────────────────────────────────────────────────────────\n");
            
            subjectDistribution.forEach((subject, count) -> {
                double percentage = teachers.size() > 0 ? (count * 100.0) / teachers.size() : 0;
                report.append(String.format("  %-20s: %2d teachers (%5.1f%%)\n", subject, count, percentage));
            });
            
            // Financial Analysis
            double totalSalary = teacherService.calculateTotalSalaryExpense();
            double avgSalary = teacherService.calculateAverageSalary();report.append("\n💰 FINANCIAL ANALYSIS\n");
            report.append("────────────────────────────────────────────────────────────\n");
            report.append(String.format("Total Monthly Salary Expense: $%,.2f\n", totalSalary));
            report.append(String.format("Average Monthly Salary: $%,.2f\n", avgSalary));
            report.append(String.format("Annual Salary Expense: $%,.2f\n", totalSalary * 12));
        }
        
        report.append("\n" + "═".repeat(64) + "\n");
        report.append("End of Teacher Statistics Report\n");
        report.append("═".repeat(64));
        
        return report.toString();
    }
    
    /**
     * ✅ الدالة المفقودة: System Summary Report
     */
    public String generateSystemSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║                 SYSTEM SUMMARY REPORT                       ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");
        
        report.append("🏫 SCHOOL MANAGEMENT SYSTEM - SPRINT 3\n");
        report.append("════════════════════════════════════════════════════════════\n");
        report.append("Version: 3.0 (Advanced OOP Course Project)\n");
        report.append("Student: Wessal Mostafa Mohammed\n");
        report.append("ID: 262504\n");
        report.append("Course: PGCS653 - Fall 2025\n");
        report.append("Report Date: ").append(LocalDate.now()).append("\n\n");
        
        report.append("🔧 MODULE STATUS\n");
        report.append("────────────────────────────────────────────────────────────\n");
        report.append("✓ User Authentication Module\n");
        report.append("✓ Student Management Module\n");
        report.append("✓ Teacher Management Module\n");
        report.append("✓ Dashboard & Analytics Module\n");
        report.append("✓ Reporting Module\n\n");
        
        report.append("═".repeat(64) + "\n");
        report.append("© 2025 Omar Al-Mukhtar University - Computer Department\n");
        report.append("═".repeat(64));
        
        return report.toString();
    }
}