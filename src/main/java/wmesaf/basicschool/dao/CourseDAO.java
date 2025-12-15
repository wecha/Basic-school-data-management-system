package wmesaf.basicschool.dao;

import wmesaf.basicschool.database.DatabaseConnection;
import wmesaf.basicschool.model.Course;
import wmesaf.basicschool.model.Teacher;
import wmesaf.basicschool.model.Student;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private Connection connection;
    private TeacherDAO teacherDAO;
    private StudentDAO studentDAO;
    
    public CourseDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.teacherDAO = new TeacherDAO();
        this.studentDAO = new StudentDAO();
        
        // إنشاء جدول المواد إذا لم يكن موجوداً
        createCoursesTable();
        createEnrollmentTable();
    }
    
    private void createCoursesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS courses (
                id INTEGER PRIMARY KEY,
                course_code TEXT UNIQUE NOT NULL,
                course_name TEXT NOT NULL,
                description TEXT,
                credit_hours INTEGER DEFAULT 3,
                department TEXT,
                start_date TEXT NOT NULL,
                end_date TEXT NOT NULL,
                max_students INTEGER DEFAULT 30,
                teacher_id INTEGER,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (teacher_id) REFERENCES teachers(person_id)
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Courses table created/verified");
        } catch (SQLException e) {
            System.err.println("❌ Error creating courses table: " + e.getMessage());
        }
    }
    
    private void createEnrollmentTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS course_enrollments (
                course_id INTEGER NOT NULL,
                student_id TEXT NOT NULL,
                enrollment_date TEXT DEFAULT CURRENT_TIMESTAMP,
                grade REAL,
                status TEXT DEFAULT 'ENROLLED',
                PRIMARY KEY (course_id, student_id),
                FOREIGN KEY (course_id) REFERENCES courses(id),
                FOREIGN KEY (student_id) REFERENCES students(student_id)
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Course enrollments table created/verified");
        } catch (SQLException e) {
            System.err.println("❌ Error creating enrollments table: " + e.getMessage());
        }
    }
    
    /**
     * إضافة مادة جديدة
     */
    public boolean addCourse(Course course) {
        System.out.println("📝 Adding course: " + course.getCourseCode());
        
        String sql = """
            INSERT INTO courses (course_code, course_name, description, credit_hours, 
                               department, start_date, end_date, max_students, teacher_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getCreditHours());
            pstmt.setString(5, course.getDepartment());
            pstmt.setString(6, course.getStartDate().toString());
            pstmt.setString(7, course.getEndDate().toString());
            pstmt.setInt(8, course.getMaxStudents());
            
            // تعيين المعلم إذا كان موجوداً
            if (course.getAssignedTeacher() != null) {
                pstmt.setInt(9, course.getAssignedTeacher().getId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        course.setId(rs.getInt(1));
                        System.out.println("✅ Course added with ID: " + course.getId());
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding course: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * الحصول على مادة بواسطة ID
     */
    public Course getCourseById(int id) {
        String sql = """
            SELECT c.*, t.teacher_id as teacher_code
            FROM courses c
            LEFT JOIN teachers t ON c.teacher_id = t.person_id
            WHERE c.id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createCourseFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting course by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * الحصول على مادة بواسطة كود المادة
     */
    public Course getCourseByCode(String courseCode) {
        String sql = """
            SELECT c.*, t.teacher_id as teacher_code
            FROM courses c
            LEFT JOIN teachers t ON c.teacher_id = t.person_id
            WHERE c.course_code = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createCourseFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting course by code: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * الحصول على جميع المواد
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = """
            SELECT c.*, t.teacher_id as teacher_code
            FROM courses c
            LEFT JOIN teachers t ON c.teacher_id = t.person_id
            ORDER BY c.course_code
            """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                if (course != null) {
                    // تحميل الطلاب المسجلين
                    loadEnrolledStudents(course);
                    courses.add(course);
                }
            }
            System.out.println("📊 Loaded " + courses.size() + " courses");
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting all courses: " + e.getMessage());
        }
        return courses;
    }
    
    /**
     * تحديث مادة
     */
    public boolean updateCourse(Course course) {
        System.out.println("✏️ Updating course: " + course.getCourseCode());
        
        String sql = """
            UPDATE courses 
            SET course_name = ?, description = ?, credit_hours = ?, department = ?,
                start_date = ?, end_date = ?, max_students = ?, teacher_id = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getDescription());
            pstmt.setInt(3, course.getCreditHours());
            pstmt.setString(4, course.getDepartment());
            pstmt.setString(5, course.getStartDate().toString());
            pstmt.setString(6, course.getEndDate().toString());
            pstmt.setInt(7, course.getMaxStudents());
            
            if (course.getAssignedTeacher() != null) {
                pstmt.setInt(8, course.getAssignedTeacher().getId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            pstmt.setInt(9, course.getId());
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Course updated: " + course.getCourseCode());
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating course: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * حذف مادة
     */
    public boolean deleteCourse(int id) {
        System.out.println("🗑️ Deleting course ID: " + id);
        
        // حذف التسجيلات أولاً
        String deleteEnrollmentsSQL = "DELETE FROM course_enrollments WHERE course_id = ?";
        String deleteCourseSQL = "DELETE FROM courses WHERE id = ?";
        
        try {
            connection.setAutoCommit(false);
            
            // حذف التسجيلات
            try (PreparedStatement pstmt = connection.prepareStatement(deleteEnrollmentsSQL)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // حذف المادة
            try (PreparedStatement pstmt = connection.prepareStatement(deleteCourseSQL)) {
                pstmt.setInt(1, id);
                int result = pstmt.executeUpdate();
                
                connection.commit();
                connection.setAutoCommit(true);
                
                System.out.println("✅ Course deleted successfully");
                return result > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("❌ Error rolling back: " + ex.getMessage());
            }
            System.err.println("❌ Error deleting course: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * تسجيل طالب في مادة
     */
    public boolean enrollStudentInCourse(int courseId, String studentId) {
        System.out.println("📚 Enrolling student " + studentId + " in course " + courseId);
        
        String sql = "INSERT INTO course_enrollments (course_id, student_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studentId);
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Student enrolled successfully");
            return result > 0;
            
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint")) {
                System.err.println("❌ Student already enrolled in this course");
            } else {
                System.err.println("❌ Error enrolling student: " + e.getMessage());
            }
        }
        return false;
    }
    
    /**
     * إزالة طالب من مادة
     */
    public boolean unenrollStudentFromCourse(int courseId, String studentId) {
        System.out.println("🚫 Unenrolling student " + studentId + " from course " + courseId);
        
        String sql = "DELETE FROM course_enrollments WHERE course_id = ? AND student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studentId);
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Student unenrolled successfully");
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error unenrolling student: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * تحميل الطلاب المسجلين في مادة
     */
    private void loadEnrolledStudents(Course course) {
        String sql = """
            SELECT s.student_id 
            FROM course_enrollments ce
            JOIN students s ON ce.student_id = s.student_id
            WHERE ce.course_id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, course.getId());
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = studentDAO.getStudentByStudentId(rs.getString("student_id"));
                if (student != null) {
                    course.enrollStudent(student);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading enrolled students: " + e.getMessage());
        }
    }
    
    /**
     * البحث عن مواد
     */
    public List<Course> searchCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        String sql = """
            SELECT c.*, t.teacher_id as teacher_code
            FROM courses c
            LEFT JOIN teachers t ON c.teacher_id = t.person_id
            WHERE c.course_code LIKE ? OR c.course_name LIKE ? OR c.description LIKE ?
            ORDER BY c.course_code
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                if (course != null) {
                    loadEnrolledStudents(course);
                    courses.add(course);
                }
            }
            System.out.println("🔍 Found " + courses.size() + " courses matching: " + keyword);
            
        } catch (SQLException e) {
            System.err.println("❌ Error searching courses: " + e.getMessage());
        }
        return courses;
    }
    
    /**
     * عدد المواد
     */
    public int countCourses() {
        String sql = "SELECT COUNT(*) as total FROM courses";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting courses: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * التحقق من وجود كود مادة
     */
    public boolean courseCodeExists(String courseCode) {
        String sql = "SELECT COUNT(*) as count FROM courses WHERE course_code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking course code: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * إنشاء كائن Course من ResultSet
     */
    private Course createCourseFromResultSet(ResultSet rs) throws SQLException {
        try {
            Course course = new Course(
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("description"),
                rs.getInt("credit_hours"),
                rs.getString("department"),
                LocalDate.parse(rs.getString("start_date")),
                LocalDate.parse(rs.getString("end_date")),
                rs.getInt("max_students")
            );
            course.setId(rs.getInt("id"));
            
            // تعيين المعلم إذا كان موجوداً
            int teacherId = rs.getInt("teacher_id");
            if (!rs.wasNull()) {
                Teacher teacher = teacherDAO.getTeacherById(teacherId);
                course.setAssignedTeacher(teacher);
            }
            
            return course;
            
        } catch (Exception e) {
            System.err.println("❌ Error creating course object: " + e.getMessage());
            throw new SQLException("Failed to create course from result set", e);
        }
    }
    
    /**
     * الحصول على مواد المعلم
     */
    public List<Course> getCoursesByTeacher(int teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = """
            SELECT c.*, t.teacher_id as teacher_code
            FROM courses c
            LEFT JOIN teachers t ON c.teacher_id = t.person_id
            WHERE c.teacher_id = ?
            ORDER BY c.course_code
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                if (course != null) {
                    loadEnrolledStudents(course);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting courses by teacher: " + e.getMessage());
        }
        return courses;
    }
    
    /**
     * الحصول على مواد الطالب
     */
    public List<Course> getCoursesByStudent(String studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = """
            SELECT c.*, t.teacher_id as teacher_code
            FROM courses c
            LEFT JOIN teachers t ON c.teacher_id = t.person_id
            JOIN course_enrollments ce ON c.id = ce.course_id
            WHERE ce.student_id = ?
            ORDER BY c.course_code
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                if (course != null) {
                    loadEnrolledStudents(course);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting courses by student: " + e.getMessage());
        }
        return courses;
    }
}