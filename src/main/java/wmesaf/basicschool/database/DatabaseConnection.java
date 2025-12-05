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
            
            // 4. إنشاء الجداول إذا لم تكن موجودة
            createTables();
            
            // 5. إضافة بيانات اختبارية
            addTestData();
            
            System.out.println("\n✅ DATABASE READY!");
            System.out.println("   - 50 Students added");
            System.out.println("   - 30 Teachers added");
            
        } catch (Exception e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTables() throws SQLException {
        System.out.println("\n🔧 Creating tables...");
        
        String[] tables = {
            // جدول admins
            "CREATE TABLE IF NOT EXISTS admins (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  username TEXT UNIQUE NOT NULL," +
            "  password TEXT NOT NULL," +
            "  full_name TEXT NOT NULL," +
            "  email TEXT NOT NULL," +
            "  is_active INTEGER DEFAULT 1," +
            "  created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")",
            
            // جدول persons
            "CREATE TABLE IF NOT EXISTS persons (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
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
            return sql.split("CREATE TABLE IF NOT EXISTS")[1].split("\\s+")[1].split("\\(")[0].trim();
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
            
            // 2. إضافة 50 طالب إنجليزي
            String checkStudents = "SELECT COUNT(*) as count FROM students";
            rs = stmt.executeQuery(checkStudents);
            
            if (rs.next() && rs.getInt("count") == 0) {
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
                    String firstName = firstNames[(i - 1) % firstNames.length];
                    String lastName = lastNames[(i - 1) % lastNames.length];
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
                    rs = stmt.executeQuery("SELECT last_insert_rowid() as id");
                    if (rs.next()) {
                        int personId = rs.getInt("id");
                        
                        // إضافة الطالب
                        String insertStudent = String.format(
                            "INSERT INTO students (person_id, student_id, grade, enrollment_date) VALUES " +
                            "(%d, '%s', '%s', '%s')",
                            personId, studentId, grade, enrollDate
                        );
                        stmt.executeUpdate(insertStudent);
                    }
                }
                System.out.println("✅ Added 50 English students");
            }
            
            // 3. إضافة 30 معلم إنجليزي
            String checkTeachers = "SELECT COUNT(*) as count FROM teachers";
            rs = stmt.executeQuery(checkTeachers);
            
            if (rs.next() && rs.getInt("count") == 0) {
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
                
                for (int i = 1; i <= 30; i++) {
                    String firstName = teacherFirstNames[(i - 1) % teacherFirstNames.length];
                    String lastName = teacherLastNames[(i - 1) % teacherLastNames.length];
                    String fullName = "Dr. " + firstName + " " + lastName;
                    String teacherId = "TCH" + String.format("%03d", i);
                    String subject = subjects[i % subjects.length];
                    double salary = 4500.00 + (i * 100);
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
                    rs = stmt.executeQuery("SELECT last_insert_rowid() as id");
                    if (rs.next()) {
                        int personId = rs.getInt("id");
                        
                        // إضافة المعلم
                        String insertTeacher = String.format(
                            "INSERT INTO teachers (person_id, teacher_id, subject, salary, hire_date) VALUES " +
                            "(%d, '%s', '%s', %.2f, '%s')",
                            personId, teacherId, subject, salary, hireDate
                        );
                        stmt.executeUpdate(insertTeacher);
                    }
                }
                System.out.println("✅ Added 30 English teachers");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding test data: " + e.getMessage());
        }
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
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM students");
            if (rs.next()) {
                System.out.println("   Students: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM teachers");
            if (rs.next()) {
                System.out.println("   Teachers: " + rs.getInt("count"));
            }
        } catch (SQLException e) {
            // تجاهل
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
    
    // دالة لإعادة ضبط قاعدة البيانات (إذا أردت حذف وإعادة إنشاء)
    public void resetDatabase() {
        System.out.println("\n🔄 Resetting database...");
        try {
            closeConnection();
            
            File dbFile = new File("school_management.db");
            if (dbFile.exists() && dbFile.delete()) {
                System.out.println("✅ Old database deleted");
            }
            
            // إعادة إنشاء الاتصال (سيخلق قاعدة بيانات جديدة)
            instance = null;
            getInstance();
            
        } catch (Exception e) {
            System.err.println("❌ Error resetting database: " + e.getMessage());
        }
    }
    
    // دالة للحصول على آخر ID متاح
    public int getNextAvailableId(String tableName) {
        String sql = "SELECT MAX(id) as max_id FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
            return 1; // إذا كانت الجدول فارغاً
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting next ID: " + e.getMessage());
            return 1;
        }
    }
}