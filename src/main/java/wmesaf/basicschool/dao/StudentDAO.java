package wmesaf.basicschool.dao;

import wmesaf.basicschool.database.DatabaseConnection;
import wmesaf.basicschool.model.PersonRecord;
import wmesaf.basicschool.model.Student;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Connection connection;
    
    public StudentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean addStudent(Student student) {
        System.out.println("📝 Adding student: " + student.getName());
        
        PersonRecord personRecord = new PersonRecord(
            student.getName(),
            student.getEmail(),
            student.getPhone(),
            student.getAddress(),
            student.getBirthDate(),
            "STUDENT"
        );
        
        PersonDAO personDAO = new PersonDAO();
        PersonRecord addedPerson = personDAO.addPerson(personRecord);
        
        if (addedPerson == null) {
            System.err.println("❌ Failed to add person record");
            return false;
        }
        
        student.setId(addedPerson.getId());
        
        String sql = "INSERT INTO students (person_id, student_id, grade, enrollment_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getStudentId());
            pstmt.setString(3, student.getGrade());
            pstmt.setString(4, student.getEnrollmentDate().toString());
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Student added: " + student.getStudentId());
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding student: " + e.getMessage());
            // Rollback: delete person if student fails
            personDAO.deletePerson(student.getId());
        }
        return false;
    }
//    public boolean addStudent(Student student) {
//    System.out.println("📝 Adding student: " + student.getName());
//    
//    PersonRecord personRecord = new PersonRecord(
//        student.getName(),
//        student.getEmail(),
//        student.getPhone(),
//        student.getAddress(),
//        student.getBirthDate(),
//        "STUDENT"
//    );
//    
//    PersonDAO personDAO = new PersonDAO();
//    PersonRecord addedPerson = personDAO.addPerson(personRecord);
//    
//    if (addedPerson == null) {
//        System.err.println("❌ Failed to add person record");
//        return false;
//    }
//    
//    // الحصول على الـ ID من الشخص المضاف
//    int personId = addedPerson.getId();
//    student.setId(personId);
//    System.out.println("✅ Person added with ID: " + personId);
//    
//    String sql = "INSERT INTO students (person_id, student_id, grade, enrollment_date) VALUES (?, ?, ?, ?)";
//    
//    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//        pstmt.setInt(1, personId);
//        pstmt.setString(2, student.getStudentId());
//        pstmt.setString(3, student.getGrade());
//        pstmt.setString(4, student.getEnrollmentDate().toString());
//        
//        int result = pstmt.executeUpdate();
//        System.out.println("✅ Student added: " + student.getStudentId() + " (Person ID: " + personId + ")");
//        return result > 0;
//        
//    } catch (SQLException e) {
//        System.err.println("❌ Error adding student: " + e.getMessage());
//        // Rollback: delete person if student fails
//        personDAO.deletePerson(personId);
//    }
//    return false;
//}
//    
//    public Student getStudentById(int id) {
//        String sql = "SELECT p.*, s.student_id, s.grade, s.enrollment_date " +
//                    "FROM persons p " +
//                    "JOIN students s ON p.id = s.person_id " +
//                    "WHERE p.id = ? AND p.type = 'STUDENT'";
//        
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, id);
//            
//            ResultSet rs = pstmt.executeQuery();
//            
//            if (rs.next()) {
//                return createStudentFromResultSet(rs);
//            }
//        } catch (SQLException e) {
//            System.err.println("❌ Error getting student: " + e.getMessage());
//        }
//        return null;
//    }
    
    public Student getStudentByStudentId(String studentId) {
        String sql = "SELECT p.*, s.student_id, s.grade, s.enrollment_date " +
                    "FROM persons p " +
                    "JOIN students s ON p.id = s.person_id " +
                    "WHERE s.student_id = ? AND p.type = 'STUDENT'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting student by ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT p.*, s.student_id, s.grade, s.enrollment_date " +
                    "FROM persons p " +
                    "JOIN students s ON p.id = s.person_id " +
                    "WHERE p.type = 'STUDENT' " +
                    "ORDER BY s.student_id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(createStudentFromResultSet(rs));
            }
            System.out.println("📊 Loaded " + students.size() + " students");
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting all students: " + e.getMessage());
        }
        return students;
    }
    
    public boolean updateStudent(Student student) {
        PersonDAO personDAO = new PersonDAO();
        if (!personDAO.updatePerson(student)) {
            return false;
        }
        
        String sql = "UPDATE students SET student_id = ?, grade = ?, enrollment_date = ? WHERE person_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getGrade());
            pstmt.setString(3, student.getEnrollmentDate().toString());
            pstmt.setInt(4, student.getId());
            
            int result = pstmt.executeUpdate();
            System.out.println("✏️ Student updated: " + student.getStudentId());
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating student: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteStudent(int id) {
        System.out.println("🗑️ Deleting student ID: " + id);
        
        String deleteStudentSQL = "DELETE FROM students WHERE person_id = ?";
        String deletePersonSQL = "DELETE FROM persons WHERE id = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt1 = connection.prepareStatement(deleteStudentSQL)) {
                pstmt1.setInt(1, id);
                pstmt1.executeUpdate();
            }
            
            try (PreparedStatement pstmt2 = connection.prepareStatement(deletePersonSQL)) {
                pstmt2.setInt(1, id);
                int rows = pstmt2.executeUpdate();
                
                connection.commit();
                connection.setAutoCommit(true);
                
                System.out.println("✅ Student deleted successfully");
                return rows > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("❌ Error rolling back: " + ex.getMessage());
            }
            System.err.println("❌ Error deleting student: " + e.getMessage());
        }
        return false;
    }
    
    public List<Student> searchStudentsByName(String name) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT p.*, s.student_id, s.grade, s.enrollment_date " +
                    "FROM persons p " +
                    "JOIN students s ON p.id = s.person_id " +
                    "WHERE p.type = 'STUDENT' AND p.name LIKE ? " +
                    "ORDER BY p.name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(createStudentFromResultSet(rs));
            }
            System.out.println("🔍 Found " + students.size() + " students matching: " + name);
            
        } catch (SQLException e) {
            System.err.println("❌ Error searching students: " + e.getMessage());
        }
        return students;
    }
    
    public List<Student> getStudentsByGrade(String grade) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT p.*, s.student_id, s.grade, s.enrollment_date " +
                    "FROM persons p " +
                    "JOIN students s ON p.id = s.person_id " +
                    "WHERE p.type = 'STUDENT' AND s.grade = ? " +
                    "ORDER BY s.student_id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, grade);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(createStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting students by grade: " + e.getMessage());
        }
        return students;
    }
    
    public int countStudents() {
        String sql = "SELECT COUNT(*) as total FROM students";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting students: " + e.getMessage());
        }
        return 0;
    }
    
    public boolean studentIdExists(String studentId) {
        String sql = "SELECT COUNT(*) as count FROM students WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking student ID: " + e.getMessage());
        }
        return false;
    }
    
    private Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        try {
            Student student = new Student(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("birth_date") != null ? LocalDate.parse(rs.getString("birth_date")) : LocalDate.now(),
                rs.getString("student_id"),
                rs.getString("grade"),
                rs.getString("enrollment_date") != null ? LocalDate.parse(rs.getString("enrollment_date")) : LocalDate.now()
            );
            student.setId(rs.getInt("id"));
            return student;
            
        } catch (Exception e) {
            System.err.println("❌ Error creating student object: " + e.getMessage());
            throw new SQLException("Failed to create student from result set", e);
        }
    }
}