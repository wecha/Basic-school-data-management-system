package wmesaf.basicschool.dao;

import wmesaf.basicschool.database.DatabaseConnection;
import wmesaf.basicschool.model.PersonRecord;
import wmesaf.basicschool.model.Teacher;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {
    private Connection connection;
    
    public TeacherDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean addTeacher(Teacher teacher) {
        System.out.println("📝 Adding teacher: " + teacher.getName());
        
        PersonRecord personRecord = new PersonRecord(
            teacher.getName(),
            teacher.getEmail(),
            teacher.getPhone(),
            teacher.getAddress(),
            teacher.getBirthDate(),
            "TEACHER"
        );
        
        PersonDAO personDAO = new PersonDAO();
        PersonRecord addedPerson = personDAO.addPerson(personRecord);
        
        if (addedPerson == null) {
            System.err.println("❌ Failed to add person record");
            return false;
        }
        
        teacher.setId(addedPerson.getId());
        
        String sql = "INSERT INTO teachers (person_id, teacher_id, subject, salary, hire_date) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacher.getId());
            pstmt.setString(2, teacher.getTeacherId());
            pstmt.setString(3, teacher.getSubject());
            pstmt.setDouble(4, teacher.getSalary());
            pstmt.setString(5, teacher.getHireDate().toString());
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Teacher added: " + teacher.getTeacherId());
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding teacher: " + e.getMessage());
            personDAO.deletePerson(teacher.getId());
        }
        return false;
    }
//    public boolean addTeacher(Teacher teacher) {
//    System.out.println("📝 Adding teacher: " + teacher.getName());
//    
//    PersonRecord personRecord = new PersonRecord(
//        teacher.getName(),
//        teacher.getEmail(),
//        teacher.getPhone(),
//        teacher.getAddress(),
//        teacher.getBirthDate(),
//        "TEACHER"
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
//    teacher.setId(personId);
//    System.out.println("✅ Person added with ID: " + personId);
//    
//    String sql = "INSERT INTO teachers (person_id, teacher_id, subject, salary, hire_date) VALUES (?, ?, ?, ?, ?)";
//    
//    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//        pstmt.setInt(1, personId);
//        pstmt.setString(2, teacher.getTeacherId());
//        pstmt.setString(3, teacher.getSubject());
//        pstmt.setDouble(4, teacher.getSalary());
//        pstmt.setString(5, teacher.getHireDate().toString());
//        
//        int result = pstmt.executeUpdate();
//        System.out.println("✅ Teacher added: " + teacher.getTeacherId() + " (Person ID: " + personId + ")");
//        return result > 0;
//        
//    } catch (SQLException e) {
//        System.err.println("❌ Error adding teacher: " + e.getMessage());
//        personDAO.deletePerson(personId);
//    }
//    return false;
//}
//    public Teacher getTeacherById(int id) {
//        String sql = "SELECT p.*, t.teacher_id, t.subject, t.salary, t.hire_date " +
//                    "FROM persons p " +
//                    "JOIN teachers t ON p.id = t.person_id " +
//                    "WHERE p.id = ? AND p.type = 'TEACHER'";
//        
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, id);
//            
//            ResultSet rs = pstmt.executeQuery();
//            
//            if (rs.next()) {
//                return createTeacherFromResultSet(rs);
//            }
//        } catch (SQLException e) {
//            System.err.println("❌ Error getting teacher: " + e.getMessage());
//        }
//        return null;
//    }
//    
    // أضف في TeacherDAO.java
public Teacher getTeacherById(int id) {
    String sql = "SELECT p.*, t.teacher_id, t.subject, t.salary, t.hire_date " +
                "FROM persons p " +
                "JOIN teachers t ON p.id = t.person_id " +
                "WHERE p.id = ? AND p.type = 'TEACHER'";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return createTeacherFromResultSet(rs);
        }
    } catch (SQLException e) {
        System.err.println("❌ Error getting teacher by ID: " + e.getMessage());
    }
    return null;
}
    public Teacher getTeacherByTeacherId(String teacherId) {
        String sql = "SELECT p.*, t.teacher_id, t.subject, t.salary, t.hire_date " +
                    "FROM persons p " +
                    "JOIN teachers t ON p.id = t.person_id " +
                    "WHERE t.teacher_id = ? AND p.type = 'TEACHER'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teacherId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createTeacherFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting teacher by ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT p.*, t.teacher_id, t.subject, t.salary, t.hire_date " +
                    "FROM persons p " +
                    "JOIN teachers t ON p.id = t.person_id " +
                    "WHERE p.type = 'TEACHER' " +
                    "ORDER BY t.teacher_id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                teachers.add(createTeacherFromResultSet(rs));
            }
            System.out.println("📊 Loaded " + teachers.size() + " teachers");
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting all teachers: " + e.getMessage());
        }
        return teachers;
    }
    
    public boolean updateTeacher(Teacher teacher) {
        PersonDAO personDAO = new PersonDAO();
        if (!personDAO.updatePerson(teacher)) {
            return false;
        }
        
        String sql = "UPDATE teachers SET teacher_id = ?, subject = ?, salary = ?, hire_date = ? WHERE person_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getTeacherId());
            pstmt.setString(2, teacher.getSubject());
            pstmt.setDouble(3, teacher.getSalary());
            pstmt.setString(4, teacher.getHireDate().toString());
            pstmt.setInt(5, teacher.getId());
            
            int result = pstmt.executeUpdate();
            System.out.println("✏️ Teacher updated: " + teacher.getTeacherId());
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating teacher: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteTeacher(int id) {
        System.out.println("🗑️ Deleting teacher ID: " + id);
        
        String deleteTeacherSQL = "DELETE FROM teachers WHERE person_id = ?";
        String deletePersonSQL = "DELETE FROM persons WHERE id = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt1 = connection.prepareStatement(deleteTeacherSQL)) {
                pstmt1.setInt(1, id);
                pstmt1.executeUpdate();
            }
            
            try (PreparedStatement pstmt2 = connection.prepareStatement(deletePersonSQL)) {
                pstmt2.setInt(1, id);
                int rows = pstmt2.executeUpdate();
                
                connection.commit();
                connection.setAutoCommit(true);
                
                System.out.println("✅ Teacher deleted successfully");
                return rows > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("❌ Error rolling back: " + ex.getMessage());
            }
            System.err.println("❌ Error deleting teacher: " + e.getMessage());
        }
        return false;
    }
    
    public List<Teacher> searchTeachersByName(String name) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT p.*, t.teacher_id, t.subject, t.salary, t.hire_date " +
                    "FROM persons p " +
                    "JOIN teachers t ON p.id = t.person_id " +
                    "WHERE p.type = 'TEACHER' AND p.name LIKE ? " +
                    "ORDER BY p.name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                teachers.add(createTeacherFromResultSet(rs));
            }
            System.out.println("🔍 Found " + teachers.size() + " teachers matching: " + name);
            
        } catch (SQLException e) {
            System.err.println("❌ Error searching teachers: " + e.getMessage());
        }
        return teachers;
    }
    
    public List<Teacher> getTeachersBySubject(String subject) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT p.*, t.teacher_id, t.subject, t.salary, t.hire_date " +
                    "FROM persons p " +
                    "JOIN teachers t ON p.id = t.person_id " +
                    "WHERE p.type = 'TEACHER' AND t.subject LIKE ? " +
                    "ORDER BY t.teacher_id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + subject + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                teachers.add(createTeacherFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting teachers by subject: " + e.getMessage());
        }
        return teachers;
    }
    
    public int countTeachers() {
        String sql = "SELECT COUNT(*) as total FROM teachers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting teachers: " + e.getMessage());
        }
        return 0;
    }
    
    public boolean teacherIdExists(String teacherId) {
        String sql = "SELECT COUNT(*) as count FROM teachers WHERE teacher_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teacherId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking teacher ID: " + e.getMessage());
        }
        return false;
    }
    
    private Teacher createTeacherFromResultSet(ResultSet rs) throws SQLException {
        try {
            Teacher teacher = new Teacher(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("birth_date") != null ? LocalDate.parse(rs.getString("birth_date")) : LocalDate.now(),
                rs.getString("teacher_id"),
                rs.getString("subject"),
                rs.getDouble("salary"),
                rs.getString("hire_date") != null ? LocalDate.parse(rs.getString("hire_date")) : LocalDate.now()
            );
            teacher.setId(rs.getInt("id"));
            return teacher;
            
        } catch (Exception e) {
            System.err.println("❌ Error creating teacher object: " + e.getMessage());
            throw new SQLException("Failed to create teacher from result set", e);
        }
    }
}