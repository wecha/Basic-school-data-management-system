package wmesaf.basicschool.business;

import wmesaf.basicschool.model.Student;
import wmesaf.basicschool.model.Teacher;
import wmesaf.basicschool.model.Course;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Service for generating various system reports.
 */
public class ReportService {
    private StudentService studentService;
    private TeacherService teacherService;
    private CourseService courseService;
    
    public ReportService() {
        this.studentService = new StudentService();
        this.teacherService = new TeacherService();
        this.courseService = new CourseService();
    }
    
    /**
     * ✅ Student Statistics Report
     */
    public String generateStudentStatisticsReport() {
        List<Student> students = studentService.getAllStudents();
        
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                  STUDENT STATISTICS REPORT                      ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n\n");
        
        // Header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        report.append("📅 Report Date: ").append(LocalDate.now().format(formatter)).append("\n");
        report.append("📊 Total Students: ").append(students.size()).append("\n\n");
        
        if (students.isEmpty()) {
            report.append("⚠️ No student data available.\n\n");
        } else {
            // Grade Distribution
            Map<String, Long> gradeDistribution = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getGrade,
                    Collectors.counting()
                ));
            
            report.append("📚 GRADE DISTRIBUTION\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            gradeDistribution.forEach((grade, count) -> {
                double percentage = students.size() > 0 ? (count * 100.0) / students.size() : 0;
                report.append(String.format("  %-15s: %3d students (%5.1f%%)\n", grade, count, percentage));
            });
            
            // Age Analysis
            report.append("\n👥 AGE ANALYSIS\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            double averageAge = studentService.calculateAverageAge();
            report.append(String.format("Average Age: %.1f years\n", averageAge));
            
            Map<String, Integer> ageGroups = Map.of(
                "Under 15", studentService.getStudentsByAgeRange(0, 14).size(),
                "15-17", studentService.getStudentsByAgeRange(15, 17).size(),
                "18-20", studentService.getStudentsByAgeRange(18, 20).size(),
                "Over 20", studentService.getStudentsByAgeRange(21, 100).size()
            );
            
            ageGroups.forEach((group, count) -> {
                double percentage = students.size() > 0 ? (count * 100.0) / students.size() : 0;
                report.append(String.format("  %-10s: %3d students (%5.1f%%)\n", group, count, percentage));
            });
            
            // Recent Enrollments
            int recentStudents = studentService.getRecentStudentsCount();
            report.append("\n🆕 RECENT ENROLLMENTS\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            report.append(String.format("Students enrolled in last 30 days: %d\n", recentStudents));
            
            // Top 10 Students by Enrollment Date (Newest)
            report.append("\n🎓 RECENTLY ENROLLED STUDENTS (Top 10)\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            students.stream()
                .sorted((s1, s2) -> s2.getEnrollmentDate().compareTo(s1.getEnrollmentDate()))
                .limit(10)
                .forEach(student -> {
                    long daysSinceEnrollment = ChronoUnit.DAYS.between(
                        student.getEnrollmentDate(), LocalDate.now());
                    report.append(String.format("  %-25s - %-15s (Enrolled %d days ago)\n",
                        student.getName(), student.getStudentId(), daysSinceEnrollment));
                });
        }
        
        report.append("\n" + "═".repeat(70) + "\n");
        report.append("End of Student Statistics Report\n");
        report.append("═".repeat(70));
        
        return report.toString();
    }
    
    /**
     * ✅ Teacher Statistics Report
     */
    public String generateTeacherStatisticsReport() {
        List<Teacher> teachers = teacherService.getAllTeachers();
        
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                 TEACHER STATISTICS REPORT                       ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n\n");
        
        // Header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        report.append("📅 Report Date: ").append(LocalDate.now().format(formatter)).append("\n");
        report.append("👩‍🏫 Total Teachers: ").append(teachers.size()).append("\n\n");
        
        if (teachers.isEmpty()) {
            report.append("⚠️ No teacher data available.\n\n");
        } else {
            // Subject Distribution
            Map<String, Long> subjectDistribution = teachers.stream()
                .collect(Collectors.groupingBy(
                    Teacher::getSubject,
                    Collectors.counting()
                ));
            
            report.append("📖 SUBJECT DISTRIBUTION\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            subjectDistribution.forEach((subject, count) -> {
                double percentage = teachers.size() > 0 ? (count * 100.0) / teachers.size() : 0;
                report.append(String.format("  %-25s: %2d teachers (%5.1f%%)\n", subject, count, percentage));
            });
            
            // Experience Analysis
            report.append("\n⏳ EXPERIENCE ANALYSIS\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            Map<String, Long> experienceGroups = teachers.stream()
                .collect(Collectors.groupingBy(
                    teacher -> {
                        int years = teacher.getYearsOfService();
                        if (years <= 5) return "0-5 years";
                        else if (years <= 10) return "6-10 years";
                        else if (years <= 20) return "11-20 years";
                        else return "20+ years";
                    },
                    Collectors.counting()
                ));
            
            experienceGroups.forEach((group, count) -> {
                double percentage = teachers.size() > 0 ? (count * 100.0) / teachers.size() : 0;
                report.append(String.format("  %-15s: %2d teachers (%5.1f%%)\n", group, count, percentage));
            });
            
            double avgExperience = teachers.stream()
                .mapToInt(Teacher::getYearsOfService)
                .average()
                .orElse(0);
            report.append(String.format("\nAverage Years of Service: %.1f years\n", avgExperience));
            
            // Financial Analysis
            double totalSalary = teacherService.calculateTotalSalaryExpense();
            double avgSalary = teacherService.calculateAverageSalary();
            
            report.append("\n💰 FINANCIAL ANALYSIS\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            report.append(String.format("Total Monthly Salary Expense: $%,.2f\n", totalSalary));
            report.append(String.format("Average Monthly Salary: $%,.2f\n", avgSalary));
            report.append(String.format("Annual Salary Expense: $%,.2f\n", totalSalary * 12));
            
            // Salary Range Analysis
            report.append("\n💵 SALARY RANGES\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            Map<String, Long> salaryRanges = teachers.stream()
                .collect(Collectors.groupingBy(
                    teacher -> {
                        double salary = teacher.getSalary();
                        if (salary < 3000) return "Under $3,000";
                        else if (salary < 4000) return "$3,000 - $3,999";
                        else if (salary < 5000) return "$4,000 - $4,999";
                        else if (salary < 6000) return "$5,000 - $5,999";
                        else return "$6,000+";
                    },
                    Collectors.counting()
                ));
            
            salaryRanges.forEach((range, count) -> {
                double percentage = teachers.size() > 0 ? (count * 100.0) / teachers.size() : 0;
                report.append(String.format("  %-20s: %2d teachers (%5.1f%%)\n", range, count, percentage));
            });
            
            // Top 5 Highest Paid Teachers
            report.append("\n🏆 TOP 5 HIGHEST PAID TEACHERS\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            teachers.stream()
                .sorted((t1, t2) -> Double.compare(t2.getSalary(), t1.getSalary()))
                .limit(5)
                .forEach(teacher -> {
                    report.append(String.format("  %-25s - %-15s - $%,.2f/month (%d years service)\n",
                        teacher.getName(), teacher.getSubject(), 
                        teacher.getSalary(), teacher.getYearsOfService()));
                });
        }
        
        report.append("\n" + "═".repeat(70) + "\n");
        report.append("End of Teacher Statistics Report\n");
        report.append("═".repeat(70));
        
        return report.toString();
    }
    
    /**
     * ✅ Course Statistics Report
     */
    public String generateCourseStatisticsReport() {
        List<Course> courses = courseService.getAllCourses();
        Map<String, Object> courseStats = courseService.getCourseStatistics();
        
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                  COURSE STATISTICS REPORT                       ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n\n");
        
        // Header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        report.append("📅 Report Date: ").append(LocalDate.now().format(formatter)).append("\n");
        report.append("📚 Total Courses: ").append(courses.size()).append("\n\n");
        
        if (courses.isEmpty()) {
            report.append("⚠️ No course data available.\n\n");
        } else {
            // Department Distribution
            @SuppressWarnings("unchecked")
            Map<String, Long> departmentDistribution = (Map<String, Long>) courseStats.get("departmentDistribution");
            
            report.append("🏛️ DEPARTMENT DISTRIBUTION\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            if (departmentDistribution != null) {
                departmentDistribution.forEach((dept, count) -> {
                    double percentage = courses.size() > 0 ? (count * 100.0) / courses.size() : 0;
                    report.append(String.format("  %-20s: %2d courses (%5.1f%%)\n", dept, count, percentage));
                });
            }
            
            // Enrollment Statistics
            report.append("\n👥 ENROLLMENT STATISTICS\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            report.append(String.format("Average Enrollment: %.1f students\n", courseStats.get("averageEnrollment")));
            report.append(String.format("Occupancy Rate: %.1f%%\n", courseStats.get("occupancyRate")));
            report.append(String.format("Full Courses: %d\n", courseStats.get("fullCourses")));
            report.append(String.format("Courses Without Teacher: %d\n", courseStats.get("coursesWithoutTeacher")));
            
            // Credit Hours Analysis
            report.append("\n⏰ CREDIT HOURS ANALYSIS\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            Map<Integer, Long> creditDistribution = courses.stream()
                .collect(Collectors.groupingBy(
                    Course::getCreditHours,
                    Collectors.counting()
                ));
            
            creditDistribution.forEach((credits, count) -> {
                double percentage = courses.size() > 0 ? (count * 100.0) / courses.size() : 0;
                report.append(String.format("  %-2d credit hours: %2d courses (%5.1f%%)\n", credits, count, percentage));
            });
            
            // Course Duration Analysis
            report.append("\n📅 COURSE DURATION\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            Map<String, Long> durationGroups = courses.stream()
                .collect(Collectors.groupingBy(
                    course -> {
                        long days = course.getDurationInDays();
                        if (days <= 30) return "≤ 1 month";
                        else if (days <= 90) return "1-3 months";
                        else if (days <= 180) return "3-6 months";
                        else return "6+ months";
                    },
                    Collectors.counting()
                ));
            
            durationGroups.forEach((duration, count) -> {
                double percentage = courses.size() > 0 ? (count * 100.0) / courses.size() : 0;
                report.append(String.format("  %-15s: %2d courses (%5.1f%%)\n", duration, count, percentage));
            });
            
            // Top 10 Most Popular Courses
            report.append("\n🏆 TOP 10 MOST POPULAR COURSES\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            courses.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getCurrentEnrollment(), c1.getCurrentEnrollment()))
                .limit(10)
                .forEach(course -> {
                    double occupancy = (double) course.getCurrentEnrollment() / course.getMaxStudents() * 100;
                    report.append(String.format("  %-10s - %-30s - %2d/%2d students (%5.1f%%)\n",
                        course.getCourseCode(), course.getCourseName(),
                        course.getCurrentEnrollment(), course.getMaxStudents(), occupancy));
                });
            
            // Courses Ending Soon (within 30 days)
            report.append("\n⏳ COURSES ENDING SOON (within 30 days)\n");
            report.append("────────────────────────────────────────────────────────────────\n");
            
            List<Course> endingSoon = courses.stream()
                .filter(course -> {
                    long daysUntilEnd = ChronoUnit.DAYS.between(LocalDate.now(), course.getEndDate());
                    return daysUntilEnd > 0 && daysUntilEnd <= 30;
                })
                .sorted((c1, c2) -> c1.getEndDate().compareTo(c2.getEndDate()))
                .toList();
            
            if (endingSoon.isEmpty()) {
                report.append("No courses ending in the next 30 days.\n");
            } else {
                endingSoon.forEach(course -> {
                    long daysUntilEnd = ChronoUnit.DAYS.between(LocalDate.now(), course.getEndDate());
                    report.append(String.format("  %-10s - %-30s - Ends in %d days\n",
                        course.getCourseCode(), course.getCourseName(), daysUntilEnd));
                });
            }
        }
        
        report.append("\n" + "═".repeat(70) + "\n");
        report.append("End of Course Statistics Report\n");
        report.append("═".repeat(70));
        
        return report.toString();
    }
    
    /**
     * ✅ System Summary Report
     */
    public String generateSystemSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                  SYSTEM SUMMARY REPORT                          ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n\n");
        
        report.append("🏫 SCHOOL MANAGEMENT SYSTEM - SPRINT 3\n");
        report.append("════════════════════════════════════════════════════════════════════\n");
        
        // System Information
        report.append("📋 SYSTEM INFORMATION\n");
        report.append("────────────────────────────────────────────────────────────────\n");
        report.append("Version: 3.0 (Advanced OOP Course Project)\n");
        report.append("Student: Wessal Mostafa Mohammed\n");
        report.append("ID: 262504\n");
        report.append("Course: PGCS653 - Fall 2025\n");
        report.append("Report Date: ").append(LocalDate.now()).append("\n");
        report.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        report.append("Database: SQLite (school_management.db)\n\n");
        
        // Module Status
        report.append("🔧 MODULE STATUS\n");
        report.append("────────────────────────────────────────────────────────────────\n");
        report.append("✓ User Authentication Module\n");
        report.append("✓ Student Management Module\n");
        report.append("✓ Teacher Management Module\n");
        report.append("✓ Course Management Module\n");
        report.append("✓ Dashboard & Analytics Module\n");
        report.append("✓ Reporting Module\n");
        report.append("✓ Database Management Module\n\n");
        
        // Current Statistics
        report.append("📈 CURRENT STATISTICS\n");
        report.append("────────────────────────────────────────────────────────────────\n");
        
        List<Student> students = studentService.getAllStudents();
        List<Teacher> teachers = teacherService.getAllTeachers();
        List<Course> courses = courseService.getAllCourses();
        
        report.append(String.format("Total Students: %d\n", students.size()));
        report.append(String.format("Total Teachers: %d\n", teachers.size()));
        report.append(String.format("Total Courses: %d\n", courses.size()));
        
        if (!teachers.isEmpty()) {
            double studentTeacherRatio = (double) students.size() / teachers.size();
            report.append(String.format("Student-Teacher Ratio: %.1f:1\n", studentTeacherRatio));
        }
        
        if (!courses.isEmpty()) {
            int totalEnrollments = courses.stream()
                .mapToInt(Course::getCurrentEnrollment)
                .sum();
            report.append(String.format("Total Course Enrollments: %d\n", totalEnrollments));
            
            double avgCoursesPerTeacher = (double) courses.size() / teachers.size();
            report.append(String.format("Average Courses per Teacher: %.1f\n", avgCoursesPerTeacher));
        }
        
        // Design Patterns Used
        report.append("\n🎨 DESIGN PATTERNS IMPLEMENTED\n");
        report.append("────────────────────────────────────────────────────────────────\n");
        report.append("✓ Singleton Pattern - DatabaseConnection\n");
        report.append("✓ Factory Pattern - PersonFactory\n");
        report.append("✓ DAO Pattern - StudentDAO, TeacherDAO, CourseDAO\n");
        report.append("✓ MVC Pattern - Four-layer architecture\n\n");
        
        // OOP Principles
        report.append("🧩 OOP PRINCIPLES APPLIED\n");
        report.append("────────────────────────────────────────────────────────────────\n");
        report.append("✓ Encapsulation - Private fields with getters/setters\n");
        report.append("✓ Inheritance - Person → Student/Teacher\n");
        report.append("✓ Polymorphism - IAuthenticatable interface\n");
        report.append("✓ Abstraction - Abstract classes and interfaces\n");
        report.append("✓ Exception Handling - Comprehensive error handling\n\n");
        
        // Recent Activities (simulated)
        report.append("🔄 RECENT SYSTEM ACTIVITIES\n");
        report.append("────────────────────────────────────────────────────────────────\n");
        
        String[] activities = {
            "Database initialized with sample data",
            "User authentication system configured",
            "Course management module deployed",
            "Reporting system enhanced",
            "Dashboard statistics updated"
        };
        
        for (String activity : activities) {
            report.append("• ").append(activity).append("\n");
        }
        
        report.append("\n" + "═".repeat(70) + "\n");
        report.append("© 2025 Omar Al-Mukhtar University - Computer Department\n");
        report.append("Project successfully completed for Sprint 3 requirements\n");
        report.append("═".repeat(70));
        
        return report.toString();
    }
    
    /**
     * ✅ Generate comprehensive report with all sections
     */
    public String generateComprehensiveReport() {
        StringBuilder report = new StringBuilder();
        
        report.append(generateSystemSummaryReport()).append("\n\n");
        report.append(generateStudentStatisticsReport()).append("\n\n");
        report.append(generateTeacherStatisticsReport()).append("\n\n");
        report.append(generateCourseStatisticsReport());
        
        return report.toString();
    }
    
    /**
     * ✅ Generate executive summary (brief report)
     */
    public String generateExecutiveSummary() {
        List<Student> students = studentService.getAllStudents();
        List<Teacher> teachers = teacherService.getAllTeachers();
        List<Course> courses = courseService.getAllCourses();
        
        double totalSalary = teacherService.calculateTotalSalaryExpense();
        double avgSalary = teacherService.calculateAverageSalary();
        
        StringBuilder summary = new StringBuilder();
        summary.append("EXECUTIVE SUMMARY\n");
        summary.append("══════════════════\n\n");
        
        summary.append("📅 Date: ").append(LocalDate.now()).append("\n\n");
        
        summary.append("KEY METRICS:\n");
        summary.append("─────────────\n");
        summary.append(String.format("• Total Students: %d\n", students.size()));
        summary.append(String.format("• Total Teachers: %d\n", teachers.size()));
        summary.append(String.format("• Total Courses: %d\n", courses.size()));
        summary.append(String.format("• Monthly Salary Expense: $%,.2f\n", totalSalary));
        summary.append(String.format("• Average Teacher Salary: $%,.2f\n\n", avgSalary));
        
        if (!teachers.isEmpty() && !students.isEmpty()) {
            double ratio = (double) students.size() / teachers.size();
            summary.append(String.format("• Student-Teacher Ratio: %.1f:1\n\n", ratio));
        }
        
        summary.append("RECOMMENDATIONS:\n");
        summary.append("────────────────\n");
        
        // تحليل وتوصيات
        if (teachers.size() < 10) {
            summary.append("• Consider hiring more teachers to improve student-teacher ratio\n");
        }
        
        if (totalSalary > 50000) {
            summary.append("• Review salary structure for cost optimization\n");
        }
        
        if (courses.size() < 5) {
            summary.append("• Expand course offerings to attract more students\n");
        } else {
            summary.append("• Maintain current course diversity and quality\n");
        }
        
        summary.append("• Continue monitoring system performance and user feedback\n");
        
        summary.append("\n" + "─".repeat(40) + "\n");
        summary.append("Prepared for: School Administration\n");
        summary.append("─".repeat(40));
        
        return summary.toString();
    }
}