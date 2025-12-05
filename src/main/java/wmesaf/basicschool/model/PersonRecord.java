package wmesaf.basicschool.model;

import java.time.LocalDate;

/**
 * A concrete implementation of Person for database operations.
 * Since Person is an abstract class, we need a concrete class
 * to instantiate objects from database records.
 */
public class PersonRecord extends Person {
    private String recordType;
    
    public PersonRecord(String name, String email, String phone, 
                       String address, LocalDate birthDate, String recordType) {
        super(name, email, phone, address, birthDate);
        setRecordType(recordType);
    }
    
    public PersonRecord(PersonRecord original) {
        super(original);
        this.recordType = original.recordType;
    }
    
    @Override
    public String getRole() {
        return recordType;
    }
    
    public String getRecordType() {
        return recordType;
    }
    
    public void setRecordType(String recordType) {
        if (recordType == null || (!recordType.equals("STUDENT") && !recordType.equals("TEACHER"))) {
            throw new IllegalArgumentException("Record type must be STUDENT or TEACHER");
        }
        this.recordType = recordType;
    }
    
    @Override
    public String toString() {
        return "PersonRecord{id=" + getId() + 
               ", name='" + getName() + '\'' +
               ", type='" + recordType + '\'' +
               ", email='" + getEmail() + '\'' +
               ", phone='" + getPhone() + '\'' +
               '}';
    }
    
    public String toDisplayString() {
        return String.format("%s [%s] - %s", getName(), recordType, getEmail());
    }
    
    public static PersonRecord createFromDatabase(int id, String name, String email, 
                                                 String phone, String address, 
                                                 LocalDate birthDate, String type) {
        PersonRecord person = new PersonRecord(name, email, phone, address, birthDate, type);
        person.setId(id);
        return person;
    }
}