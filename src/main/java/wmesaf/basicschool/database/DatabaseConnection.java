package wmesaf.basicschool.database;

import java.sql.*;
import java.io.File;

public class DatabaseConnection {
    private static final String SQLITE_URL = "jdbc:sqlite:school_management.db";
    
    private Connection connection;
    private static DatabaseConnection instance;

    public DatabaseConnection() {
        System.out.println("\n🚀 INITIALIZING SCHOOL DATABASE");
        System.out.println("================================\n");
        
        try {
            // 1. تحميل السائق
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ SQLite Driver loaded");
            
            // 2. الاتصال بقاعدة البيانات
            connection = DriverManager.getConnection(SQLITE_URL);
            System.out.println("✅ Connected to: school_management.db");
            
            // 3. تفعيل المفاتيح الأجنبية
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                System.out.println("✅ Foreign keys enabled");
            }
            
            // 4. إنشاء جميع الجداول
            createTables();
            
            // 5. إضافة بيانات اختبارية
            addTestData();
            
            System.out.println("\n✅ DATABASE READY!");
            
        } catch (Exception e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTables() throws SQLException {
        System.out.println("\n🔧 Creating all tables...");
        
        String[] tables = {
            // جدول admins
            "CREATE TABLE IF NOT EXISTS admins (" +
            "  id INTEGER PRIMARY KEY," +
            "  username TEXT UNIQUE NOT NULL," +
            "  password TEXT NOT NULL," +
            "  full_name TEXT NOT NULL," +
            "  email TEXT NOT NULL," +
            "  is_active INTEGER DEFAULT 1," +
            "  created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")",
            
            // جدول persons
            "CREATE TABLE IF NOT EXISTS persons (" +
            "  id INTEGER PRIMARY KEY," +
            "  type TEXT NOT NULL CHECK(type IN ('STUDENT', 'TEACHER'))," +
            "  name TEXT NOT NULL," +
            "  email TEXT," +
            "  phone TEXT," +
            "  address TEXT," +
            "  birth_date TEXT," +
            "  created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")",
            
            // جدول students
            "CREATE TABLE IF NOT EXISTS students (" +
            "  person_id INTEGER PRIMARY KEY," +
            "  student_id TEXT UNIQUE NOT NULL," +
            "  grade TEXT," +
            "  enrollment_date TEXT," +
            "  FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE" +
            ")",
            
            // جدول teachers
            "CREATE TABLE IF NOT EXISTS teachers (" +
            "  person_id INTEGER PRIMARY KEY," +
            "  teacher_id TEXT UNIQUE NOT NULL," +
            "  subject TEXT," +
            "  salary REAL," +
            "  hire_date TEXT," +
            "  FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE" +
            ")",
            
            // جدول courses (جديد)
            "CREATE TABLE IF NOT EXISTS courses (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  course_code TEXT UNIQUE NOT NULL," +
            "  course_name TEXT NOT NULL," +
            "  description TEXT," +
            "  credit_hours INTEGER DEFAULT 3," +
            "  department TEXT," +
            "  start_date TEXT NOT NULL," +
            "  end_date TEXT NOT NULL," +
            "  max_students INTEGER DEFAULT 30," +
            "  teacher_id INTEGER," +
            "  created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (teacher_id) REFERENCES teachers(person_id)" +
            ")",
            
            // جدول تسجيلات المواد (جديد)
            "CREATE TABLE IF NOT EXISTS course_enrollments (" +
            "  course_id INTEGER NOT NULL," +
            "  student_id TEXT NOT NULL," +
            "  enrollment_date TEXT DEFAULT CURRENT_TIMESTAMP," +
            "  grade REAL," +
            "  status TEXT DEFAULT 'ENROLLED'," +
            "  PRIMARY KEY (course_id, student_id)," +
            "  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
            "  FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE" +
            ")"
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String tableSQL : tables) {
                stmt.execute(tableSQL);
                String tableName = extractTableName(tableSQL);
                System.out.println("✅ Table: " + tableName);
            }
        }
    }
    
    private String extractTableName(String sql) {
        try {
            // استخراج اسم الجدول من SQL
            if (sql.contains("CREATE TABLE IF NOT EXISTS")) {
                String temp = sql.split("CREATE TABLE IF NOT EXISTS")[1];
                return temp.split("\\s+")[1].split("\\(")[0].trim();
            } else if (sql.contains("CREATE TABLE")) {
                String temp = sql.split("CREATE TABLE")[1];
                return temp.split("\\s+")[1].split("\\(")[0].trim();
            }
            return "table";
        } catch (Exception e) {
            return "table";
        }
    }
    
    private void addTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // 1. إضافة admin افتراضي
            String checkAdmin = "SELECT COUNT(*) as count FROM admins";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            
            if (rs.next() && rs.getInt("count") == 0) {
                String insertAdmin = "INSERT INTO admins (username, password, full_name, email) VALUES " +
                                   "('admin', 'admin123', 'System Administrator', 'admin@school.com')";
                stmt.executeUpdate(insertAdmin);
                System.out.println("✅ Default admin created");
            }
            
            // 2. التحقق من وجود بيانات الطلاب
            String checkStudents = "SELECT COUNT(*) as count FROM students";
            rs = stmt.executeQuery(checkStudents);
            
            if (rs.next() && rs.getInt("count") == 0) {
                System.out.println("\n📊 Adding test students...");
                addSampleStudents(stmt);
                System.out.println("✅ Added sample students");
            }
            
            // 3. التحقق من وجود بيانات المعلمين
            String checkTeachers = "SELECT COUNT(*) as count FROM teachers";
            rs = stmt.executeQuery(checkTeachers);
            
            if (rs.next() && rs.getInt("count") == 0) {
                System.out.println("\n👨‍🏫 Adding test teachers...");
                addSampleTeachers(stmt);
                System.out.println("✅ Added sample teachers");
            }
            
            // 4. التحقق من وجود بيانات المواد (جديد)
            String checkCourses = "SELECT COUNT(*) as count FROM courses";
            rs = stmt.executeQuery(checkCourses);
            
            if (rs.next() && rs.getInt("count") == 0) {
                System.out.println("\n📚 Adding sample courses...");
                addSampleCourses(stmt);
                System.out.println("✅ Added sample courses with enrollments");
            }
            
            // 5. عرض الإحصائيات النهائية
            System.out.println("\n📋 FINAL DATABASE STATISTICS:");
            System.out.println("   Persons: " + getCount("persons"));
            System.out.println("   Students: " + getCount("students"));
            System.out.println("   Teachers: " + getCount("teachers"));
            System.out.println("   Courses: " + getCount("courses"));
            System.out.println("   Course Enrollments: " + getCount("course_enrollments"));
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding test data: " + e.getMessage());
        }
    }
    
    private void addSampleStudents(Statement stmt) throws SQLException {
        String[] firstNames = {"John", "Emma", "Michael", "Sophia", "James", "Olivia", "Robert", "Ava", 
                              "William", "Isabella", "David", "Mia", "Richard", "Charlotte", "Joseph", 
                              "Amelia", "Thomas", "Harper", "Charles", "Evelyn", "Christopher", "Abigail",
                              "Daniel", "Emily", "Matthew", "Elizabeth", "Anthony", "Sofia", "Donald", "Madison"};
        
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
                             "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
                             "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson",
                             "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson"};
        
        String[] grades = {"9th Grade", "10th Grade", "11th Grade", "12th Grade"};
        String[] addresses = {"123 Main St, New York", "456 Oak Ave, Los Angeles", "789 Pine Rd, Chicago",
                             "321 Elm St, Houston", "654 Maple Dr, Phoenix", "987 Cedar Ln, Philadelphia"};
        
        for (int i = 1; i <= 50; i++) {
            String firstName = firstNames[(i-1) % firstNames.length];
            String lastName = lastNames[(i-1) % lastNames.length];
            String fullName = firstName + " " + lastName;
            String studentId = "STU" + String.format("%03d", i);
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@school.com";
            String phone = "555-01" + String.format("%02d", i);
            String address = addresses[i % addresses.length];
            String birthDate = (2004 + (i % 5)) + "-" + String.format("%02d", (i % 12) + 1) + "-" + String.format("%02d", (i % 28) + 1);
            String grade = grades[i % grades.length];
            String enrollDate = "2023-09-01";
            
            // إضافة الشخص
            String insertPerson = String.format(
                "INSERT INTO persons (type, name, email, phone, address, birth_date) VALUES " +
                "('STUDENT', '%s', '%s', '%s', '%s', '%s')",
                fullName, email, phone, address, birthDate
            );
            stmt.executeUpdate(insertPerson);
            
            // الحصول على آخر ID
            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
            int personId = rs.next() ? rs.getInt(1) : i;
            
            // إضافة الطالب
            String insertStudent = String.format(
                "INSERT INTO students (person_id, student_id, grade, enrollment_date) VALUES " +
                "(%d, '%s', '%s', '%s')",
                personId, studentId, grade, enrollDate
            );
            stmt.executeUpdate(insertStudent);
        }
    }
    
    private void addSampleTeachers(Statement stmt) throws SQLException {
        String[] teacherFirstNames = {"Alexander", "Benjamin", "Christopher", "Daniel", "Edward", 
                                     "Franklin", "George", "Henry", "Isaac", "Jacob", "Kevin", "Liam",
                                     "Nathan", "Oliver", "Patrick", "Quincy", "Richard", "Samuel",
                                     "Theodore", "Ulysses", "Victor", "Walter", "Xavier", "Yusuf", "Zachary"};
        
        String[] teacherLastNames = {"Adams", "Baker", "Carter", "Davis", "Edwards", "Foster", "Green",
                                    "Harris", "Irwin", "Johnson", "King", "Lewis", "Miller", "Nelson",
                                    "Owens", "Parker", "Quinn", "Roberts", "Scott", "Turner", "Underwood"};
        
        String[] subjects = {"Mathematics", "Physics", "Chemistry", "Biology", "English Literature",
                            "History", "Geography", "Computer Science", "Art", "Music", "Physical Education",
                            "Economics", "Business Studies", "Psychology", "Sociology", "Foreign Languages"};
        
        String[] teacherAddresses = {"101 Teacher St, Boston", "202 Educator Ave, Seattle", "303 Professor Rd, Miami",
                                    "404 Scholar Ln, Atlanta", "505 Academy Dr, Denver", "606 Campus St, Austin"};
        
        for (int i = 1; i <= 15; i++) {
            String firstName = teacherFirstNames[(i-1) % teacherFirstNames.length];
            String lastName = teacherLastNames[(i-1) % teacherLastNames.length];
            String fullName = "Dr. " + firstName + " " + lastName;
            String teacherId = "TCH" + String.format("%03d", i);
            String subject = subjects[(i-1) % subjects.length];
            double salary = 4500.00 + (i * 150);
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@school.edu";
            String phone = "555-02" + String.format("%02d", i);
            String address = teacherAddresses[i % teacherAddresses.length];
            String birthDate = (1970 + (i % 30)) + "-" + String.format("%02d", (i % 12) + 1) + "-" + String.format("%02d", (i % 28) + 1);
            String hireDate = (2010 + (i % 15)) + "-" + String.format("%02d", ((i % 9) + 1)) + "-" + String.format("%02d", ((i % 20) + 1));
            
            // إضافة الشخص
            String insertPerson = String.format(
                "INSERT INTO persons (type, name, email, phone, address, birth_date) VALUES " +
                "('TEACHER', '%s', '%s', '%s', '%s', '%s')",
                fullName, email, phone, address, birthDate
            );
            stmt.executeUpdate(insertPerson);
            
            // الحصول على آخر ID
            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
            int personId = rs.next() ? rs.getInt(1) : 50 + i;
            
            // إضافة المعلم
            String insertTeacher = String.format(
                "INSERT INTO teachers (person_id, teacher_id, subject, salary, hire_date) VALUES " +
                "(%d, '%s', '%s', %.2f, '%s')",
                personId, teacherId, subject, salary, hireDate
            );
            stmt.executeUpdate(insertTeacher);
        }
    }
    
    private void addSampleCourses(Statement stmt) throws SQLException {
        // أولاً: الحصول على قائمة المعلمين
        String[] teacherIds = new String[15];
        ResultSet rs = stmt.executeQuery("SELECT teacher_id FROM teachers ORDER BY teacher_id");
        int teacherIndex = 0;
        while (rs.next()) {
            teacherIds[teacherIndex++] = rs.getString("teacher_id");
        }
        
        // مواد عينة
        String[][] sampleCourses = {
            {"CS101", "Introduction to Programming", "Basic programming concepts using Java", "3", "Computer Science"},
            {"CS201", "Data Structures", "Fundamental data structures and algorithms", "4", "Computer Science"},
            {"CS301", "Database Systems", "Design and implementation of database systems", "3", "Computer Science"},
            {"MATH101", "Calculus I", "Differential and integral calculus", "4", "Mathematics"},
            {"ENG101", "English Composition", "Academic writing and communication skills", "3", "Languages"},
            {"PHYS101", "General Physics", "Mechanics, thermodynamics, and waves", "4", "Physics"},
            {"CHEM101", "General Chemistry", "Atomic structure, chemical bonding, and reactions", "4", "Chemistry"},
            {"BIO101", "General Biology", "Cell biology, genetics, and evolution", "4", "Biology"},
            {"HIST101", "World History", "Major historical events and civilizations", "3", "History"},
            {"ART101", "Introduction to Art", "Art history, theory, and basic techniques", "3", "Arts"}
        };
        
        String currentDate = java.time.LocalDate.now().toString();
        String startDate = currentDate;
        String endDate = java.time.LocalDate.now().plusMonths(4).toString();
        
        for (int i = 0; i < sampleCourses.length; i++) {
            String[] course = sampleCourses[i];
            String teacherId = (i < teacherIds.length) ? teacherIds[i] : teacherIds[0];
            
            // الحصول على person_id للمعلم
            String getTeacherPersonId = String.format(
                "SELECT person_id FROM teachers WHERE teacher_id = '%s'",
                teacherId
            );
            rs = stmt.executeQuery(getTeacherPersonId);
            int teacherPersonId = rs.next() ? rs.getInt("person_id") : 1;
            
            // إضافة المادة
            String insertCourse = String.format(
                "INSERT INTO courses (course_code, course_name, description, credit_hours, " +
                "department, start_date, end_date, max_students, teacher_id) VALUES " +
                "('%s', '%s', '%s', %s, '%s', '%s', '%s', %d, %d)",
                course[0], course[1], course[2], course[3], course[4],
                startDate, endDate, 30, teacherPersonId
            );
            stmt.executeUpdate(insertCourse);
            
            // الحصول على course_id
            rs = stmt.executeQuery("SELECT last_insert_rowid()");
            int courseId = rs.next() ? rs.getInt(1) : i + 1;
            
            // تسجيل بعض الطلاب في المادة
            addSampleEnrollments(stmt, courseId, Math.min(15, 50));
        }
    }
    
    private void addSampleEnrollments(Statement stmt, int courseId, int numberOfStudents) throws SQLException {
        // الحصول على قائمة الطلاب
        ResultSet rs = stmt.executeQuery(
            "SELECT student_id FROM students ORDER BY RANDOM() LIMIT " + numberOfStudents
        );
        
        while (rs.next()) {
            String studentId = rs.getString("student_id");
            String insertEnrollment = String.format(
                "INSERT OR IGNORE INTO course_enrollments (course_id, student_id) VALUES (%d, '%s')",
                courseId, studentId
            );
            stmt.executeUpdate(insertEnrollment);
        }
    }
    
    private int getCount(String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tableName)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
    
    public boolean isConnectionValid() {
        if (connection == null) return false;
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public String getDatabaseInfo() {
        return "SQLite (school_management.db)";
    }
    
    public void printDatabaseStatus() {
        System.out.println("\n📋 DATABASE STATUS:");
        System.out.println("   Type: SQLite");
        System.out.println("   File: school_management.db");
        System.out.println("   Connection: " + (isConnectionValid() ? "ACTIVE" : "INACTIVE"));
        
        try (Statement stmt = connection.createStatement()) {
            // الطلاب
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM students");
            if (rs.next()) {
                System.out.println("   Students: " + rs.getInt("count"));
            }
            
            // المعلمون
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM teachers");
            if (rs.next()) {
                System.out.println("   Teachers: " + rs.getInt("count"));
            }
            
            // المواد (جديد)
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM courses");
            if (rs.next()) {
                System.out.println("   Courses: " + rs.getInt("count"));
            }
            
            // التسجيلات (جديد)
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM course_enrollments");
            if (rs.next()) {
                System.out.println("   Enrollments: " + rs.getInt("count"));
            }
            
            // آخر ID للأشخاص
            rs = stmt.executeQuery("SELECT COALESCE(MAX(id), 0) as max_id FROM persons");
            if (rs.next()) {
                System.out.println("   Last Person ID: " + rs.getInt("max_id"));
            }
            
        } catch (SQLException e) {
            // تجاهل الأخطاء في طباعة الحالة
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // تجاهل
        }
    }
    
    public void resetDatabase() {
        System.out.println("\n🔄 Resetting database...");
        try {
            closeConnection();
            
            File dbFile = new File("school_management.db");
            if (dbFile.exists() && dbFile.delete()) {
                System.out.println("✅ Old database deleted");
                System.out.println("⚠️ Please restart the application to create a new database");
            } else {
                System.out.println("❌ Could not delete database file");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error resetting database: " + e.getMessage());
        }
    }
    
    public int getNextAvailableId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 as next_id FROM persons";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int nextId = rs.getInt("next_id");
                System.out.println("🔢 Next available Person ID: " + nextId);
                return nextId;
            }
            return 1;
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting next ID: " + e.getMessage());
            return 1;
        }
    }
    
    /**
     * طريقة مساعدة للحصول على إحصائيات قاعدة البيانات
     */
    public String getDatabaseStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("Database Statistics:\n");
        stats.append("===================\n");
        
        try (Statement stmt = connection.createStatement()) {
            String[] tables = {"persons", "students", "teachers", "courses", "course_enrollments", "admins"};
            String[] labels = {"Persons", "Students", "Teachers", "Courses", "Enrollments", "Admins"};
            
            for (int i = 0; i < tables.length; i++) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tables[i]);
                if (rs.next()) {
                    stats.append(String.format("%-15s: %d\n", labels[i], rs.getInt("count")));
                }
            }
            
        } catch (SQLException e) {
            stats.append("Error getting statistics: ").append(e.getMessage());
        }
        
        return stats.toString();
    }
}