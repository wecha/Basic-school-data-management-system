package wmesaf.basicschool.model;

import java.time.LocalDate;

/**
 * Represents a teacher in the school management system.
 * Extends the abstract Person class with teacher-specific properties
 * such as subject, salary, and hire date.
 * Demonstrates inheritance, polymorphism, and encapsulation principles.
 * 
 * @author wmesaf
 * @version 1.0
 * @see Person
 * @see Student
 */
public class Teacher extends Person {
    private String teacherId;
    private String subject;
    private double salary;
    private LocalDate hireDate;
    
    /**
     * Constructs a new Teacher with specified details.
     * 
     * @param name the teacher's full name
     * @param email the teacher's email address
     * @param phone the teacher's phone number
     * @param address the teacher's physical address
     * @param birthDate the teacher's date of birth
     * @param teacherId the unique teacher identifier
     * @param subject the subject taught by the teacher
     * @param salary the teacher's salary
     * @param hireDate the date the teacher was hired
     */
    public Teacher(String name, String email, String phone, String address,
                   LocalDate birthDate, String teacherId, String subject, 
                   double salary, LocalDate hireDate) {
        super(name, email, phone, address, birthDate);
        this.teacherId = teacherId;
        this.subject = subject;
        this.salary = salary;
        this.hireDate = hireDate;
    }
    
    /**
     * Copy constructor for Teacher.
     * Creates a deep copy of an existing Teacher object.
     * 
     * @param original the Teacher object to copy
     * @throws IllegalArgumentException if original is null
     */
    public Teacher(Teacher original) {
        super(original);
        this.teacherId = original.teacherId;
        this.subject = original.subject;
        this.salary = original.salary;
        this.hireDate = original.hireDate;
    }
    
    /**
     * Returns the role of this person as "Teacher".
     * Implements the abstract method from Person class.
     * 
     * @return the string "Teacher"
     */
    @Override
    public String getRole() {
        return "Teacher";
    }
    
    // Getters and Setters with Javadoc
    
    /**
     * Returns the teacher's unique identifier.
     * 
     * @return the teacher ID
     */
    public String getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the teacher's unique identifier.
     * 
     * @param teacherId the new teacher ID
     * @throws IllegalArgumentException if teacherId is null or empty
     */
    public void setTeacherId(String teacherId) {
        if (teacherId == null || teacherId.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher ID cannot be null or empty");
        }
        this.teacherId = teacherId;
    }

    /**
     * Returns the subject taught by the teacher.
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject taught by the teacher.
     * 
     * @param subject the new subject
     * @throws IllegalArgumentException if subject is null or empty
     */
    public void setSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        this.subject = subject;
    }

    /**
     * Returns the teacher's salary.
     * 
     * @return the salary
     */
    public double getSalary() {
        return salary;
    }

    /**
     * Sets the teacher's salary.
     * 
     * @param salary the new salary
     * @throws IllegalArgumentException if salary is negative
     */
    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.salary = salary;
    }

    /**
     * Returns the teacher's hire date.
     * 
     * @return the hire date
     */
    public LocalDate getHireDate() {
        return hireDate;
    }

    /*** Sets the teacher's hire date.
     * 
     * @param hireDate the new hire date
     * @throws IllegalArgumentException if hireDate is in the future
     */
    public void setHireDate(LocalDate hireDate) {
        if (hireDate == null || hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be null or in the future");
        }
        this.hireDate = hireDate;
    }
    
    /**
     * Returns a formatted salary string with currency.
     * 
     * @return formatted salary string
     */
    public String getFormattedSalary() {
        return String.format("$%,.2f", salary);
    }
    
    /**
     * Calculates the teacher's years of service.
     * 
     * @return years of service
     */
    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return LocalDate.now().getYear() - hireDate.getYear();
    }
    
    /**
     * Returns a string representation of the Teacher object.
     * Includes teacher-specific information along with inherited properties.
     * 
     * @return a string representation of the teacher
     */
    @Override
    public String toString() {
        return "Teacher{" + 
               "teacherId='" + teacherId + '\'' + 
               ", subject='" + subject + '\'' + 
               ", salary=" + getFormattedSalary() + 
               ", hireDate=" + hireDate + 
               ", yearsOfService=" + getYearsOfService() +
               ", " + super.toString() + 
               '}';
    }
}