package wmesaf.basicschool.business;

import wmesaf.basicschool.dao.StudentDAO;
import wmesaf.basicschool.dao.TeacherDAO;
import wmesaf.basicschool.dao.CourseDAO;
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
    private CourseDAO courseDAO;
    private StudentService studentService;
    private TeacherService teacherService;
    private CourseService courseService;
    
    public DashboardService() {
        this.studentDAO = new StudentDAO();
        this.teacherDAO = new TeacherDAO();
        this.courseDAO = new CourseDAO();
        this.studentService = new StudentService();
        this.teacherService = new TeacherService();
        this.courseService = new CourseService();
    }
    
    /**
     * ✅ Get all dashboard statistics
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic Statistics
        stats.put("totalStudents", studentDAO.countStudents());
        stats.put("totalTeachers", teacherDAO.countTeachers());
        stats.put("totalCourses", courseDAO.countCourses());
        stats.put("totalPersons", studentDAO.countStudents() + teacherDAO.countTeachers());
        
        // Course Statistics
        Map<String, Object> courseStats = courseService.getCourseStatistics();
        stats.put("averageCourseEnrollment", courseStats.get("averageEnrollment"));
        stats.put("courseOccupancyRate", courseStats.get("occupancyRate"));
        stats.put("fullCourses", courseStats.get("fullCourses"));
        stats.put("coursesWithoutTeacher", courseStats.get("coursesWithoutTeacher"));
        
        // Financial Stats
        stats.put("totalSalary", teacherService.calculateTotalSalaryExpense());
        stats.put("averageSalary", teacherService.calculateAverageSalary());
        
        // Student Stats
        stats.put("averageStudentAge", studentService.calculateAverageAge());
        stats.put("recentStudents", studentService.getRecentStudentsCount());
        
        // Teacher Stats
        stats.put("teachersByExperience", teacherService.getTeachersByExperience(0, 5).size());
        
        // System Stats
        stats.put("systemStatus", "🟢 Operational");
        stats.put("databaseStatus", getDatabaseStatus());
        stats.put("lastUpdate", LocalDate.now().toString());
        stats.put("uptimeDays", calculateUptimeDays());
        
        return stats;
    }
    
    /**
     * ✅ Get database connection status
     */
    private String getDatabaseStatus() {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            if (db.isConnectionValid()) {
                return "🟢 Connected";
            } else {
                return "🔴 Disconnected";
            }
        } catch (Exception e) {
            return "🔴 Error: " + e.getMessage();
        }
    }
    
    /**
     * ✅ Calculate system uptime (simulated)
     */
    private int calculateUptimeDays() {
        // في النظام الحقيقي، يمكن قراءة هذا من ملف log
        // هنا نستخدم قيمة ثابتة للعرض
        return 45; // 45 يوم
    }
    
    /**
     * ✅ Generate dashboard report
     */
    public String generateDashboardReport() {
        Map<String, Object> stats = getDashboardStatistics();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(60)).append("\n");
        report.append("           SCHOOL MANAGEMENT DASHBOARD REPORT\n");
        report.append("=".repeat(60)).append("\n\n");
        
        report.append("📊 BASIC STATISTICS:\n");
        report.append("-".repeat(50)).append("\n");
        report.append(String.format("Total Students: %d\n", stats.get("totalStudents")));
        report.append(String.format("Total Teachers: %d\n", stats.get("totalTeachers")));
        report.append(String.format("Total Courses: %d\n", stats.get("totalCourses")));
        report.append(String.format("Total Records: %d\n\n", stats.get("totalPersons")));
        
        report.append("📚 COURSE STATISTICS:\n");
        report.append("-".repeat(50)).append("\n");
        report.append(String.format("Average Course Enrollment: %.1f students\n", stats.get("averageCourseEnrollment")));
        report.append(String.format("Course Occupancy Rate: %.1f%%\n", stats.get("courseOccupancyRate")));
        report.append(String.format("Full Courses: %d\n", stats.get("fullCourses")));
        report.append(String.format("Courses Without Teacher: %d\n\n", stats.get("coursesWithoutTeacher")));
        
        report.append("💰 FINANCIAL OVERVIEW:\n");
        report.append("-".repeat(50)).append("\n");
        report.append(String.format("Total Monthly Salary Expense: $%,.2f\n", stats.get("totalSalary")));
        report.append(String.format("Average Teacher Salary: $%,.2f\n", stats.get("averageSalary")));
        report.append(String.format("Annual Salary Expense: $%,.2f\n\n", (double)stats.get("totalSalary") * 12));
        
        report.append("👨‍🎓 STUDENT ANALYTICS:\n");
        report.append("-".repeat(50)).append("\n");
        report.append(String.format("Average Student Age: %.1f years\n", stats.get("averageStudentAge")));
        report.append(String.format("Recent Students (last 30 days): %d\n\n", stats.get("recentStudents")));
        
        report.append("👩‍🏫 TEACHER ANALYTICS:\n");
        report.append("-".repeat(50)).append("\n");
        report.append(String.format("Teachers with 0-5 years experience: %d\n\n", stats.get("teachersByExperience")));
        
        report.append("🔧 SYSTEM STATUS:\n");
        report.append("-".repeat(50)).append("\n");
        report.append(String.format("System Status: %s\n", stats.get("systemStatus")));
        report.append(String.format("Database: %s\n", stats.get("databaseStatus")));
        report.append(String.format("System Uptime: %d days\n", stats.get("uptimeDays")));
        report.append(String.format("Last Update: %s\n\n", stats.get("lastUpdate")));
        
        report.append("📈 PERFORMANCE SUMMARY:\n");
        report.append("-".repeat(50)).append("\n");
        
        // تحليل الأداء
        int totalStudents = (int) stats.get("totalStudents");
        int totalTeachers = (int) stats.get("totalTeachers");
        int totalCourses = (int) stats.get("totalCourses");
        
        if (totalTeachers > 0) {
            double studentTeacherRatio = (double) totalStudents / totalTeachers;
            report.append(String.format("Student-Teacher Ratio: %.1f:1\n", studentTeacherRatio));
        }
        
        if (totalCourses > 0) {
            double coursesPerTeacher = (double) totalCourses / totalTeachers;
            report.append(String.format("Average Courses per Teacher: %.1f\n", coursesPerTeacher));
        }
        
        double occupancyRate = (double) stats.get("courseOccupancyRate");
        if (occupancyRate > 80) {
            report.append("📢 High demand for courses! Consider adding more sections.\n");
        } else if (occupancyRate < 30) {
            report.append("⚠️ Low course enrollment. Consider marketing strategies.\n");
        }
        
        report.append("\n" + "=".repeat(60)).append("\n");
        report.append("Report generated: ").append(LocalDate.now());
        report.append("\n").append("=".repeat(60));
        
        return report.toString();
    }
    
    /**
     * ✅ Get quick stats for dashboard display
     */
    public Map<String, String> getQuickStats() {
        Map<String, String> quickStats = new HashMap<>();
        
        Map<String, Object> fullStats = getDashboardStatistics();
        
        quickStats.put("students", String.valueOf(fullStats.get("totalStudents")));
        quickStats.put("teachers", String.valueOf(fullStats.get("totalTeachers")));
        quickStats.put("courses", String.valueOf(fullStats.get("totalCourses")));
        quickStats.put("salary", String.format("$%,.2f", fullStats.get("totalSalary")));
        quickStats.put("status", (String) fullStats.get("systemStatus"));
        quickStats.put("occupancy", String.format("%.1f%%", fullStats.get("courseOccupancyRate")));
        
        return quickStats;
    }
    
    /**
     * ✅ Get statistics by department (simulated)
     */
    public Map<String, Integer> getDepartmentStatistics() {
        Map<String, Integer> deptStats = new HashMap<>();
        
        // في نظام حقيقي، يمكن الحصول على هذه البيانات من قاعدة البيانات
        // هنا نستخدم بيانات افتراضية للعرض
        
        deptStats.put("Computer Science", 8);
        deptStats.put("Mathematics", 6);
        deptStats.put("Physics", 4);
        deptStats.put("Chemistry", 3);
        deptStats.put("Biology", 5);
        deptStats.put("Languages", 7);
        deptStats.put("History", 4);
        deptStats.put("Arts", 3);
        
        return deptStats;
    }
    
    /**
     * ✅ Get enrollment trends (simulated)
     */
    public Map<String, Integer> getEnrollmentTrend() {
        Map<String, Integer> trend = new HashMap<>();
        
        // بيانات افتراضية لاتجاهات التسجيل في آخر 6 أشهر
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String monthKey = month.getMonth().toString().substring(0, 3) + " " + month.getYear();
            
            // أرقام عشوائية للعرض (في نظام حقيقي تأتي من قاعدة البيانات)
            int enrollment = 120 + (int)(Math.random() * 30);
            trend.put(monthKey, enrollment);
        }
        
        return trend;
    }
}