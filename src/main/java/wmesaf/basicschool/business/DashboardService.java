package wmesaf.basicschool.business;

import wmesaf.basicschool.dao.StudentDAO;
import wmesaf.basicschool.dao.TeacherDAO;
import wmesaf.basicschool.database.DatabaseConnection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for dashboard statistics and analytics.
 */
public class DashboardService {
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private StudentService studentService;
    private TeacherService teacherService;
    
    public DashboardService() {
        this.studentDAO = new StudentDAO();
        this.teacherDAO = new TeacherDAO();
        this.studentService = new StudentService();
        this.teacherService = new TeacherService();
    }
    
    /**
     * ✅ الدالة المفقودة: Get all dashboard statistics
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic Statistics
        stats.put("totalStudents", studentDAO.countStudents());
        stats.put("totalTeachers", teacherDAO.countTeachers());
        stats.put("totalPersons", studentDAO.countStudents() + teacherDAO.countTeachers());
        
        // Financial Stats
        stats.put("totalSalary", teacherService.calculateTotalSalaryExpense());
        stats.put("averageSalary", teacherService.calculateAverageSalary());
        
        // System Stats
        stats.put("systemStatus", "🟢 Operational");
        stats.put("databaseStatus", getDatabaseStatus());
        stats.put("lastUpdate", LocalDate.now().toString());
        
        return stats;
    }
    
    /**
     * ✅ الدالة المفقودة: Get database connection status
     */
    private String getDatabaseStatus() {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            return db.isConnectionValid() ? "🟢 Connected" : "🔴 Disconnected";
        } catch (Exception e) {
            return "🔴 Error";
        }
    }
    
    /**
     * ✅ الدالة المفقودة: Generate dashboard report
     */
    public String generateDashboardReport() {
        Map<String, Object> stats = getDashboardStatistics();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(50)).append("\n");
        report.append("          DASHBOARD STATISTICS REPORT\n");
        report.append("=".repeat(50)).append("\n\n");
        
        report.append("📊 BASIC STATISTICS:\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("Total Students: %d\n", stats.get("totalStudents")));
        report.append(String.format("Total Teachers: %d\n", stats.get("totalTeachers")));
        report.append(String.format("Total Records: %d\n\n", stats.get("totalPersons")));
        
        report.append("💰 FINANCIAL OVERVIEW:\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("Total Salary Expense: $%,.2f\n", stats.get("totalSalary")));
        report.append(String.format("Average Teacher Salary: $%,.2f\n\n", stats.get("averageSalary")));
        
        report.append("🔧 SYSTEM STATUS:\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("System Status: %s\n", stats.get("systemStatus")));
        report.append(String.format("Database: %s\n", stats.get("databaseStatus")));
        report.append(String.format("Last Update: %s\n\n", stats.get("lastUpdate")));
        
        report.append("=".repeat(50)).append("\n");
        report.append("Report generated: ").append(LocalDate.now());
        report.append("\n").append("=".repeat(50));
        
        return report.toString();
    }
}