package wmesaf.basicschool.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * نموذج يمثل مادة دراسية في النظام
 */
public class Course {
    private int id;
    private String courseCode;
    private String courseName;
    private String description;
    private int creditHours;
    private String department;
    private LocalDate startDate;
    private LocalDate endDate;
    private int maxStudents;
    private Teacher assignedTeacher;
    private List<Student> enrolledStudents;
    
    /**
     * مُنشئ كامل
     */
    public Course(String courseCode, String courseName, String description, 
                  int creditHours, String department, LocalDate startDate, 
                  LocalDate endDate, int maxStudents) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.creditHours = creditHours;
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxStudents = maxStudents;
        this.enrolledStudents = new ArrayList<>();
    }
    
    /**
     * مُنشئ النسخ
     */
    public Course(Course original) {
        this.id = original.id;
        this.courseCode = original.courseCode;
        this.courseName = original.courseName;
        this.description = original.description;
        this.creditHours = original.creditHours;
        this.department = original.department;
        this.startDate = original.startDate;
        this.endDate = original.endDate;
        this.maxStudents = original.maxStudents;
        this.assignedTeacher = original.assignedTeacher;
        this.enrolledStudents = new ArrayList<>(original.enrolledStudents);
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { 
        this.courseCode = courseCode; 
    }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { 
        this.courseName = courseName; 
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { 
        if (creditHours < 1 || creditHours > 5) {
            throw new IllegalArgumentException("Credit hours must be between 1 and 5");
        }
        this.creditHours = creditHours; 
    }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { 
        this.department = department; 
    }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { 
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.startDate = startDate; 
    }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { 
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.endDate = endDate; 
    }
    
    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { 
        if (maxStudents < 1) {
            throw new IllegalArgumentException("Max students must be at least 1");
        }
        this.maxStudents = maxStudents; 
    }
    
    public Teacher getAssignedTeacher() { return assignedTeacher; }
    public void setAssignedTeacher(Teacher teacher) { 
        this.assignedTeacher = teacher; 
    }
    
    public List<Student> getEnrolledStudents() { 
        return new ArrayList<>(enrolledStudents); // Return copy for encapsulation
    }// ==================== BUSINESS METHODS ====================
    
    /**
     * إضافة طالب للمادة
     */
    public boolean enrollStudent(Student student) {
        if (enrolledStudents.size() >= maxStudents) {
            throw new IllegalStateException("Course is full. Maximum capacity: " + maxStudents);
        }
        
        if (enrolledStudents.contains(student)) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }
        
        enrolledStudents.add(student);
        return true;
    }
    
    /**
     * إزالة طالب من المادة
     */
    public boolean unenrollStudent(Student student) {
        return enrolledStudents.remove(student);
    }
    
    /**
     * عدد الطلاب المسجلين حالياً
     */
    public int getCurrentEnrollment() {
        return enrolledStudents.size();
    }
    
    /**
     * أماكن متاحة
     */
    public int getAvailableSeats() {
        return maxStudents - enrolledStudents.size();
    }
    
    /**
     * هل المادة ممتلئة؟
     */
    public boolean isFull() {
        return enrolledStudents.size() >= maxStudents;
    }
    
    /**
     * هل الطالب مسجل في المادة؟
     */
    public boolean hasStudent(Student student) {
        return enrolledStudents.contains(student);
    }
    
    /**
     * مدة المادة بالأيام
     */
    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    @Override
    public String toString() {
        return String.format("Course{%s - %s, Credits: %d, Teacher: %s, Students: %d/%d}",
            courseCode, courseName, creditHours,
            (assignedTeacher != null ? assignedTeacher.getName() : "Not Assigned"),
            enrolledStudents.size(), maxStudents);
    }
    
    /**
     * تمثيل مختصر للمادة
     */
    public String toShortString() {
        return courseCode + " - " + courseName;
    }
    
    /**
     * تمثيل كامل للمادة
     */
    public String toFullString() {
        return String.format("""
            Course Details:
            ===============
            Code: %s
            Name: %s
            Credits: %d
            Department: %s
            Description: %s
            Period: %s to %s (%d days)
            Teacher: %s
            Enrollment: %d/%d students
            Available Seats: %d
            """, 
            courseCode, courseName, creditHours, department, 
            (description != null ? description : "No description"),
            startDate, endDate, getDurationInDays(),
            (assignedTeacher != null ? assignedTeacher.getName() + " (" + assignedTeacher.getTeacherId() + ")" : "Not assigned"),
            enrolledStudents.size(), maxStudents, getAvailableSeats());
    }
}