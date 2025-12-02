
package wmesaf.basicschool.model;

import java.time.LocalDate;

/**
 * Represents a student in the school management system.
 * Extends the abstract Person class with student-specific attributes.
 * Demonstrates inheritance, polymorphism, and encapsulation.
 * 
 * @author wmesaf
 * @version 1.0
 * @see Person
 */
public class Student extends Person {
    private String studentId;
    private String grade;
    private LocalDate enrollmentDate;
    
    /**
     * Constructs a new Student with specified details.
     * 
     * @param name the student's full name
     * @param email the student's email address
     * @param phone the student's phone number
     * @param address the student's physical address
     * @param birthDate the student's date of birth
     * @param studentId the unique student identifier
     * @param grade the student's current grade level
     * @param enrollmentDate the date the student enrolled
     */
    public Student(String name, String email, String phone, String address,
                   LocalDate birthDate, String studentId, String grade, 
                   LocalDate enrollmentDate) {
        super(name, email, phone, address, birthDate);
        this.studentId = studentId;
        this.grade = grade;
        this.enrollmentDate = enrollmentDate;
    }
    
    /**
     * Copy constructor for Student.
     * Creates a deep copy of an existing Student object.
     * 
     * @param original the Student to copy
     */
    public Student(Student original) {
        super(original);
        this.studentId = original.studentId;
        this.grade = original.grade;
        this.enrollmentDate = original.enrollmentDate;
    }
    
    /**
     * Returns the role of this person as "Student".
     * Implements the abstract method from Person class.
     * 
     * @return the string "Student"
     */
    @Override
    public String getRole() {
        return "Student";
    }
    
    // Getters and Setters with Javadoc
    
    /**
     * Returns the student ID.
     * 
     * @return the student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     * 
     * @param studentId the new student ID
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     * Returns the student's grade level.
     * 
     * @return the grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the student's grade level.
     * 
     * @param grade the new grade
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Returns the enrollment date.
     * 
     * @return the enrollment date
     */
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    /**
     * Sets the enrollment date.
     * 
     * @param enrollmentDate the new enrollment date
     */
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    /**
     * Returns a string representation of the Student.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return "Student{" + 
               "studentId='" + studentId + '\'' + 
               ", grade='" + grade + '\'' + 
               ", enrollmentDate=" + enrollmentDate + 
               ", " + super.toString() + 
               '}';
    }
}