package wmesaf.basicschool.dao;

import wmesaf.basicschool.database.DatabaseConnection;
import wmesaf.basicschool.model.Person;
import wmesaf.basicschool.model.PersonRecord;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    private Connection connection;
    
    public PersonDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public PersonRecord addPerson(Person person) {
        System.out.println("📝 Adding person to database: " + person.getName());
        
        // الحصول على ID التالي المتسلسل
        int nextId = getNextPersonId();
        
        String sql = "INSERT INTO persons (id, type, name, email, phone, address, birth_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, nextId);
            pstmt.setString(2, person.getRole().toUpperCase());
            pstmt.setString(3, person.getName());
            pstmt.setString(4, person.getEmail());
            pstmt.setString(5, person.getPhone());
            pstmt.setString(6, person.getAddress());
            
            // Handle birth date
            if (person.getBirthDate() != null) {
                pstmt.setString(7, person.getBirthDate().toString());
            } else {
                pstmt.setString(7, LocalDate.now().toString());
            }
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("✅ Person added with ID: " + nextId);
            
            if (affectedRows > 0) {
                return PersonRecord.createFromDatabase(
                    nextId,
                    person.getName(),
                    person.getEmail(),
                    person.getPhone(),
                    person.getAddress(),
                    person.getBirthDate(),
                    person.getRole().toUpperCase()
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding person: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private int getNextPersonId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 as next_id FROM persons";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int nextId = rs.getInt("next_id");
                System.out.println("🔢 Next available Person ID: " + nextId);
                return nextId;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting next person ID: " + e.getMessage());
        }
        return 1;
    }
    
    public PersonRecord getPersonById(int id) {
        String sql = "SELECT * FROM persons WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createPersonFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting person: " + e.getMessage());
        }
        return null;
    }
    
    public List<PersonRecord> getAllPersons() {
        List<PersonRecord> persons = new ArrayList<>();
        String sql = "SELECT * FROM persons ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                persons.add(createPersonFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all persons: " + e.getMessage());
        }
        return persons;
    }
    
    public boolean updatePerson(Person person) {
        System.out.println("✏️ Updating person ID: " + person.getId());
        
        String sql = "UPDATE persons SET name = ?, email = ?, phone = ?, address = ?, birth_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, person.getName());
            pstmt.setString(2, person.getEmail());
            pstmt.setString(3, person.getPhone());
            pstmt.setString(4, person.getAddress());
            
            // Handle birth date
            if (person.getBirthDate() != null) {
                pstmt.setString(5, person.getBirthDate().toString());
            } else {
                pstmt.setString(5, LocalDate.now().toString());
            }
            
            pstmt.setInt(6, person.getId());
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Person updated, rows affected: " + result);
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating person: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deletePerson(int id) {
        System.out.println("🗑️ Deleting person ID: " + id);
        
        String sql = "DELETE FROM persons WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Person deleted, rows affected: " + result);
            
            // بعد الحذف، نعيد ترتيب الـ IDs
            if (result > 0) {
                reorganizePersonIds();
            }
            
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting person: " + e.getMessage());
        }
        return false;
    }
    
    private void reorganizePersonIds() {
        System.out.println("🔄 Reorganizing person IDs...");
        
        try {
            // 1. إنشاء جدول مؤقت
            String createTemp = "CREATE TABLE IF NOT EXISTS persons_temp (" +
                              "id INTEGER PRIMARY KEY," +
                              "type TEXT NOT NULL," +
                              "name TEXT NOT NULL," +
                              "email TEXT," +
                              "phone TEXT," +
                              "address TEXT," +
                              "birth_date TEXT," +
                              "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                              ")";
            
            Statement stmt = connection.createStatement();
            stmt.execute(createTemp);
            
            // 2. نسخ البيانات بترتيب جديد
            String copyData = "INSERT INTO persons_temp (type, name, email, phone, address, birth_date, created_at) " +
                            "SELECT type, name, email, phone, address, birth_date, created_at " +
                            "FROM persons ORDER BY id";
            stmt.executeUpdate(copyData);
            
            // 3. حذف الجدول القديم
            stmt.execute("DROP TABLE persons");
            
            // 4. إعادة تسمية الجدول المؤقت
            stmt.execute("ALTER TABLE persons_temp RENAME TO persons");
            
            System.out.println("✅ Person IDs reorganized successfully");
            
        } catch (SQLException e) {
            System.err.println("❌ Error reorganizing person IDs: " + e.getMessage());
        }
    }
    
    public List<PersonRecord> searchPersonsByName(String name) {
        List<PersonRecord> persons = new ArrayList<>();
        String sql = "SELECT * FROM persons WHERE LOWER(name) LIKE ? ORDER BY name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name.toLowerCase() + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                persons.add(createPersonFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching persons: " + e.getMessage());
        }
        return persons;
    }
    
    public int countPersons() {
        String sql = "SELECT COUNT(*) as total FROM persons";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting persons: " + e.getMessage());
        }
        return 0;
    }
    
    public int getMaxPersonId() {
        String sql = "SELECT COALESCE(MAX(id), 0) as max_id FROM persons";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("max_id");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting max person ID: " + e.getMessage());
        }
        return 0;
    }
    
    private PersonRecord createPersonFromResultSet(ResultSet rs) throws SQLException {
        LocalDate birthDate = null;
        try {
            String birthDateStr = rs.getString("birth_date");
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                birthDate = LocalDate.parse(birthDateStr);
            }
        } catch (Exception e) {
            System.err.println("❌ Error parsing birth date: " + e.getMessage());
        }
        
        return PersonRecord.createFromDatabase(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            birthDate,
            rs.getString("type")
        );
    }
}