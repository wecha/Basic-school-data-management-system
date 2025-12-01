package wmesaf.basicschool.model;

import java.time.LocalDate;

/**
 * Abstract base class representing a Person in the school management system.
 * This class serves as the parent class for all person types (Student, Teacher, etc.)
 * and demonstrates key OOP concepts including abstraction, encapsulation, and inheritance.
 * 
 * @author wmesaf
 * @version 1.0
 * @since 2025
 */
public abstract class Person {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthDate;
    
    /**
     * Constructs a new Person with the specified details.
     * 
     * @param name      the person's full name
     * @param email     the person's email address
     * @param phone     the person's phone number
     * @param address   the person's physical address
     * @param birthDate the person's date of birth
     * @throws IllegalArgumentException if name is null or empty
     */
    public Person(String name, String email, String phone, 
                  String address, LocalDate birthDate) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
    }
    
    /**
     * Copy constructor that creates a deep copy of an existing Person object.
     * This constructor ensures that the new object is independent of the original.
     * 
     * @param original the Person object to copy
     * @throws IllegalArgumentException if original is null
     */
    public Person(Person original) {
        if (original == null) {
            throw new IllegalArgumentException("Original Person cannot be null");
        }
        
        this.id = original.id;
        this.name = original.name;
        this.email = original.email;
        this.phone = original.phone;
        this.address = original.address;
        this.birthDate = original.birthDate;
        // Note: LocalDate is immutable, so direct reference copy is safe
    }
    
    /**
     * Abstract method that must be implemented by subclasses to return 
     * the role of the person in the school system.
     * 
     * @return a string representing the person's role (e.g., "Student", "Teacher")
     */
    public abstract String getRole();
    
    /**
     * Returns the unique identifier of the person.
     * 
     * @return the person's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the person.
     * 
     * @param id the new ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the person's full name.
     * 
     * @return the person's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the person's full name.
     * 
     * @param name the new name to set
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Returns the person's email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the person's email address.
     * 
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the person's phone number.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the person's phone number.
     * 
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the person's physical address.
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the person's physical address.
     * 
     * @param address the new address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the person's date of birth.
     * 
     * @return the birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the person's date of birth.
     * 
     * @param birthDate the new birth date
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    /**
     * Returns a string representation of the Person object.
     * The format includes the ID, name, email, and phone number.
     * 
     * @return a string representation of the person
     */
    @Override
    public String toString() {
        return "Person{" + 
               "id=" + id + 
               ", name='" + name + '\'' + 
               ", email='" + email + '\'' + 
               ", phone='" + phone + '\'' + 
               ", role='" + getRole() + '\'' +
               '}';
    }
}