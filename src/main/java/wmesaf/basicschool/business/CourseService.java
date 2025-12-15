package wmesaf.basicschool.business;

import wmesaf.basicschool.dao.CourseDAO;
import wmesaf.basicschool.dao.TeacherDAO;
import wmesaf.basicschool.dao.StudentDAO;
import wmesaf.basicschool.model.Course;
import wmesaf.basicschool.model.Teacher;
import wmesaf.basicschool.model.Student;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service لإدارة العمليات المتعلقة بالمواد الدراسية
 */
public class CourseService {
    private CourseDAO courseDAO;
    private TeacherDAO teacherDAO;
    private StudentDAO studentDAO;
    
    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.teacherDAO = new TeacherDAO();
        this.studentDAO = new StudentDAO();
    }
    
    /**
     * إضافة مادة جديدة مع التحقق من القواعد
     */
    public boolean addCourse(Course course) {
        // التحقق من المدخلات
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Course code is required");
        }
        
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Course name is required");
        }
        
        if (course.getStartDate() == null || course.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        
        // التحقق من تاريخ البداية والنهاية
        if (course.getStartDate().isAfter(course.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        // التحقق من تاريخ البداية (لا يمكن في الماضي البعيد)
        if (course.getStartDate().isBefore(LocalDate.now().minusMonths(6))) {
            throw new IllegalArgumentException("Start date cannot be more than 6 months in the past");
        }
        
        // التحقق من عدد الساعات المعتمدة
        if (course.getCreditHours() < 1 || course.getCreditHours() > 5) {
            throw new IllegalArgumentException("Credit hours must be between 1 and 5");
        }
        
        // التحقق من الحد الأقصى للطلاب
        if (course.getMaxStudents() < 1 || course.getMaxStudents() > 100) {
            throw new IllegalArgumentException("Max students must be between 1 and 100");
        }
        
        // التحقق من عدم وجود كود مادة مكرر
        if (courseDAO.courseCodeExists(course.getCourseCode())) {
            throw new IllegalArgumentException("Course code '" + course.getCourseCode() + "' already exists");
        }
        
        // التحقق من وجود المعلم إذا تم تعيينه
        if (course.getAssignedTeacher() != null) {
            Teacher teacher = teacherDAO.getTeacherById(course.getAssignedTeacher().getId());
            if (teacher == null) {
                throw new IllegalArgumentException("Assigned teacher does not exist in the system");
            }
        }
        
        return courseDAO.addCourse(course);
    }
    
    /**
     * الحصول على جميع المواد
     */
    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }
    
    /**
     * الحصول على مادة بواسطة ID
     */
    public Course getCourseById(int id) {
        return courseDAO.getCourseById(id);
    }
    
    /**
     * الحصول على مادة بواسطة الكود
     */
    public Course getCourseByCode(String courseCode) {
        return courseDAO.getCourseByCode(courseCode);
    }
    
    /**
     * تحديث مادة
     */
    public boolean updateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        return courseDAO.updateCourse(course);
    }
    
    /**
     * حذف مادة
     */
    public boolean deleteCourse(int id) {
        return courseDAO.deleteCourse(id);
    }
    
    /**
     * تسجيل طالب في مادة
     */
    public boolean enrollStudentInCourse(int courseId, String studentId) {
        Course course = courseDAO.getCourseById(courseId);
        Student student = studentDAO.getStudentByStudentId(studentId);
        
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found");
        }
        
        // التحقق من أن المادة ليست ممتلئة
        if (course.isFull()) {
            throw new IllegalStateException("Course is full. No available seats");
        }
        
        // التحقق من أن الطالب غير مسجل بالفعل
        if (course.hasStudent(student)) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }
        
        // التحقق من أن الطالب لا يأخذ أكثر من 18 ساعة معتمدة
        if (getStudentCreditHours(studentId) + course.getCreditHours() > 18) {
            throw new IllegalArgumentException("Student cannot exceed 18 credit hours");
        }
        
        return courseDAO.enrollStudentInCourse(courseId, studentId);
    }
    
    /**
     * إزالة طالب من مادة
     */
    public boolean unenrollStudentFromCourse(int courseId, String studentId) {
        return courseDAO.unenrollStudentFromCourse(courseId, studentId);
    }
    
    /**
     * البحث عن مواد
     */
    public List<Course> searchCourses(String keyword) {
        return courseDAO.searchCourses(keyword);
    }
    
    /**
     * الحصول على مواد المعلم
     */
    public List<Course> getCoursesByTeacher(int teacherId) {
        return courseDAO.getCoursesByTeacher(teacherId);
    }
    
    /**
     * الحصول على مواد الطالب
     */
    public List<Course> getCoursesByStudent(String studentId) {
        return courseDAO.getCoursesByStudent(studentId);
    }
    
    /**
     * الحصول على عدد المواد
     */
    public int countCourses() {
        return courseDAO.countCourses();
    }
    
    /**
     * التحقق من وجود كود مادة
     */
    public boolean courseCodeExists(String courseCode) {
        return courseDAO.courseCodeExists(courseCode);
    }
    
    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * حساب إجمالي الساعات المعتمدة للطالب
     */
    public int getStudentCreditHours(String studentId) {
        List<Course> studentCourses = getCoursesByStudent(studentId);
        return studentCourses.stream()
            .mapToInt(Course::getCreditHours)
            .sum();
    }
    
    /**
     * الحصول على إحصائيات المواد
     */
    public Map<String, Object> getCourseStatistics() {
        List<Course> allCourses = getAllCourses();
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalCourses", allCourses.size());
        
        if (!allCourses.isEmpty()) {
            // متوسط عدد الطلاب
            double avgEnrollment = allCourses.stream()
                .mapToInt(Course::getCurrentEnrollment)
                .average()
                .orElse(0);
            stats.put("averageEnrollment", avgEnrollment);
            
            // نسبة الامتلاء
            double occupancyRate = allCourses.stream()
                .mapToDouble(c -> (double) c.getCurrentEnrollment() / c.getMaxStudents() * 100)
                .average()
                .orElse(0);
            stats.put("occupancyRate", occupancyRate);
            
            // المواد الممتلئة
            long fullCourses = allCourses.stream()
                .filter(Course::isFull)
                .count();
            stats.put("fullCourses", fullCourses);
            
            // توزيع المواد حسب القسم
            Map<String, Long> departmentDistribution = allCourses.stream()
                .collect(Collectors.groupingBy(
                    Course::getDepartment,
                    Collectors.counting()
                ));
            stats.put("departmentDistribution", departmentDistribution);
            
            // المواد بدون معلم
            long coursesWithoutTeacher = allCourses.stream()
                .filter(c -> c.getAssignedTeacher() == null)
                .count();
            stats.put("coursesWithoutTeacher", coursesWithoutTeacher);
        }
        
        return stats;
    }
    
    /**
     * توليد تقرير المواد
     */
    public String generateCourseReport() {
        List<Course> courses = getAllCourses();
        Map<String, Object> stats = getCourseStatistics();
        
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║                    COURSE MANAGEMENT REPORT                 ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");
        
        report.append("📅 Report Date: ").append(LocalDate.now()).append("\n");
        report.append("📚 Total Courses: ").append(courses.size()).append("\n\n");
        
        report.append("📊 COURSE STATISTICS\n");
        report.append("────────────────────────────────────────────────────────────\n");
        
        report.append(String.format("Total Courses: %d\n", stats.get("totalCourses")));
        report.append(String.format("Average Enrollment: %.1f students\n", stats.get("averageEnrollment")));
        report.append(String.format("Occupancy Rate: %.1f%%\n", stats.get("occupancyRate")));
        report.append(String.format("Full Courses: %d\n", stats.get("fullCourses")));
        report.append(String.format("Courses Without Teacher: %d\n\n", stats.get("coursesWithoutTeacher")));
        
        // توزيع الأقسام
        @SuppressWarnings("unchecked")
        Map<String, Long> deptDist = (Map<String, Long>) stats.get("departmentDistribution");
        if (deptDist != null && !deptDist.isEmpty()) {
            report.append("📖 DEPARTMENT DISTRIBUTION\n");
            report.append("────────────────────────────────────────────────────────────\n");
            deptDist.forEach((dept, count) -> {
                double percentage = courses.size() > 0 ? (count * 100.0) / courses.size() : 0;
                report.append(String.format("  %-20s: %2d courses (%5.1f%%)\n", dept, count, percentage));
            });
            report.append("\n");
        }
        
        // قائمة المواد
        if (!courses.isEmpty()) {
            report.append("📋 COURSE LIST\n");
            report.append("────────────────────────────────────────────────────────────\n");
            
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                report.append(String.format("%2d. %-10s - %-30s (Enrolled: %2d/%2d)\n",
                    i + 1,
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCurrentEnrollment(),
                    course.getMaxStudents()));
            }
        }
        
        report.append("\n" + "═".repeat(64) + "\n");
        report.append("End of Course Management Report\n");
        report.append("═".repeat(64));
        
        return report.toString();
    }
    
    /**
     * إضافة بيانات تجريبية للمواد
     */
    public void addSampleCourses() {
        if (countCourses() > 0) {
            System.out.println("📊 Courses already exist in database");
            return;
        }
        
        System.out.println("📚 Adding sample courses...");
        
        LocalDate now = LocalDate.now();
        List<Teacher> teachers = teacherDAO.getAllTeachers();
        
        // مواد عينة
        Course[] sampleCourses = {
            new Course("CS101", "Introduction to Programming", 
                      "Basic programming concepts using Java", 3, "Computer Science",
                      now.plusDays(7), now.plusMonths(4), 30),
            
            new Course("CS201", "Data Structures", 
                      "Fundamental data structures and algorithms", 4, "Computer Science",
                      now.plusDays(7), now.plusMonths(4), 25),
            
            new Course("CS301", "Database Systems", 
                      "Design and implementation of database systems", 3, "Computer Science",
                      now.plusDays(7), now.plusMonths(4), 20),
            
            new Course("MATH101", "Calculus I", 
                      "Differential and integral calculus", 4, "Mathematics",
                      now.plusDays(7), now.plusMonths(4), 35),
            
            new Course("ENG101", "English Composition", 
                      "Academic writing and communication skills", 3, "Languages",
                      now.plusDays(7), now.plusMonths(4), 40),
            
            new Course("PHYS101", "General Physics", 
                      "Mechanics, thermodynamics, and waves", 4, "Physics",
                      now.plusDays(7), now.plusMonths(4), 30),
            
            new Course("CHEM101", "General Chemistry", 
                      "Atomic structure, chemical bonding, and reactions", 4, "Chemistry",
                      now.plusDays(7), now.plusMonths(4), 25),
            
            new Course("BIO101", "General Biology", 
                      "Cell biology, genetics, and evolution", 4, "Biology",
                      now.plusDays(7), now.plusMonths(4), 30),
            
            new Course("HIST101", "World History", 
                      "Major historical events and civilizations", 3, "History",
                      now.plusDays(7), now.plusMonths(4), 45),
            
            new Course("ART101", "Introduction to Art", 
                      "Art history, theory, and basic techniques", 3, "Arts",
                      now.plusDays(7), now.plusMonths(4), 25)
        };
        
        // تعيين معلمين عشوائيين للمواد
        for (int i = 0; i < sampleCourses.length && i < teachers.size(); i++) {
            sampleCourses[i].setAssignedTeacher(teachers.get(i));
        }
        
        // إضافة المواد
        int added = 0;
        for (Course course : sampleCourses) {
            if (addCourse(course)) {
                added++;
                
                // تسجيل بعض الطلاب عشوائياً
                List<Student> students = studentDAO.getAllStudents();
                int enrollCount = Math.min(10, students.size());
                for (int i = 0; i < enrollCount; i++) {
                    try {
                        enrollStudentInCourse(course.getId(), students.get(i).getStudentId());
                    } catch (Exception e) {
                        // تجاهل الأخطاء (مثل تسجيل مكرر)
                    }
                }
            }
        }
        
        System.out.println("✅ Added " + added + " sample courses with student enrollments");
    }
}