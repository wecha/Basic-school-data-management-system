package wmesaf.basicschool.gui;

import wmesaf.basicschool.business.CourseService;
import wmesaf.basicschool.business.TeacherService;
import wmesaf.basicschool.business.StudentService;
import wmesaf.basicschool.model.Course;
import wmesaf.basicschool.model.Teacher;
import wmesaf.basicschool.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import wmesaf.basicschool.database.DatabaseConnection;

public class CourseManagementFrame extends JFrame {
    private CourseService courseService;
    private TeacherService teacherService;
    private StudentService studentService;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addSampleDataButton;
    
    public CourseManagementFrame() {
        courseService = new CourseService();
        teacherService = new TeacherService();
        studentService = new StudentService();
        initUI();
        setupFrame();
        loadCourses();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // ========== TOP PANEL ==========
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // العنوان
        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(142, 68, 173));
        
        // شريط البحث
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(142, 68, 173));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchCourses());
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        String[] buttonNames = {"Add Course", "Edit Course", "Delete Course", 
                               "Manage Students", "View Details", "Refresh"};
        Color[] buttonColors = {
            new Color(46, 204, 113),
            new Color(241, 196, 15),
            new Color(231, 76, 60),
            new Color(52, 152, 219),
            new Color(155, 89, 182),
            new Color(149, 165, 166)
        };
        
        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = createActionButton(buttonNames[i], buttonColors[i]);
            buttonPanel.add(button);
        }
        
        // زر إضافة بيانات تجريبية
        addSampleDataButton = new JButton("➕ Add Sample Courses");
        addSampleDataButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addSampleDataButton.setBackground(new Color(243, 156, 18));
        addSampleDataButton.setForeground(Color.WHITE);
        addSampleDataButton.addActionListener(e -> addSampleCourses());
        buttonPanel.add(addSampleDataButton);
        
        // ========== TABLE ==========
        String[] columns = {"Course Code", "Course Name", "Credits", "Department", 
                           "Teacher", "Students", "Start Date", "End Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseTable.getTableHeader().setBackground(new Color(142, 68, 173));
        courseTable.getTableHeader().setForeground(Color.WHITE);
        
        courseTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewCourseDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        // ========== ASSEMBLE ==========
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 40));
        
        switch (text) {
            case "Add Course":
                button.addActionListener(e -> addCourse());
                break;
            case "Edit Course":
                button.addActionListener(e -> editCourse());
                break;
            case "Delete Course":
                button.addActionListener(e -> deleteCourse());
                break;
            case "Manage Students":
                button.addActionListener(e -> manageCourseStudents());
                break;
            case "View Details":
                button.addActionListener(e -> viewCourseDetails());
                break;
            case "Refresh":
                button.addActionListener(e -> loadCourses());
                break;
        }
        
        return button;
    }
    
    private void loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseService.getAllCourses();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Course course : courses) {
            Object[] row = {
                course.getCourseCode(),
                course.getCourseName(),
                course.getCreditHours(),
                course.getDepartment(),
                (course.getAssignedTeacher() != null ? course.getAssignedTeacher().getName() : "Not Assigned"),
                String.format("%d/%d", course.getCurrentEnrollment(), course.getMaxStudents()),
                course.getStartDate().format(formatter),
                course.getEndDate().format(formatter)
            };
            tableModel.addRow(row);
        }
        
        searchField.setText("");
        addSampleDataButton.setVisible(courses.isEmpty());
    }
    
    private void searchCourses() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadCourses();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Course> courses = courseService.searchCourses(searchTerm);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No courses found matching: " + searchTerm,
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        for (Course course : courses) {
            Object[] row = {
                course.getCourseCode(),
                course.getCourseName(),
                course.getCreditHours(),
                course.getDepartment(),
                (course.getAssignedTeacher() != null ? course.getAssignedTeacher().getName() : "Not Assigned"),
                String.format("%d/%d", course.getCurrentEnrollment(), course.getMaxStudents()),
                course.getStartDate().format(formatter),
                course.getEndDate().format(formatter)
            };
            tableModel.addRow(row);
        }
    }
    
    private void addCourse() {
        JDialog dialog = new JDialog(this, "Add New Course", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField creditsField = new JTextField("3");
        JTextField deptField = new JTextField("Computer Science");
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        
        JTextField startDateField = new JTextField(LocalDate.now().plusDays(7).toString());
        JTextField endDateField = new JTextField(LocalDate.now().plusMonths(4).toString());
        JTextField maxStudentsField = new JTextField("30");
        
        JComboBox<String> teacherCombo = new JComboBox<>();
        teacherCombo.addItem("-- Select Teacher --");
        List<Teacher> teachers = teacherService.getAllTeachers();
        for (Teacher teacher : teachers) {
            teacherCombo.addItem(teacher.getTeacherId() + " - " + teacher.getName());
        }
        
        formPanel.add(new JLabel("Course Code *:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Course Name *:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Credit Hours:"));
        formPanel.add(creditsField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        formPanel.add(startDateField);
        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        formPanel.add(endDateField);
        formPanel.add(new JLabel("Max Students:"));
        formPanel.add(maxStudentsField);
        formPanel.add(new JLabel("Assign Teacher:"));
        formPanel.add(teacherCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save Course");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        
        saveButton.addActionListener(e -> {
            try {
                if (codeField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please fill in required fields (Course Code and Name)",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (courseService.courseCodeExists(codeField.getText().trim())) {
                    JOptionPane.showMessageDialog(dialog,
                        "Course code already exists. Please use a different code.",
                        "Duplicate Code",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Course newCourse = new Course(
                    codeField.getText().trim(),
                    nameField.getText().trim(),
                    descArea.getText().trim(),
                    Integer.parseInt(creditsField.getText().trim()),
                    deptField.getText().trim(),
                    LocalDate.parse(startDateField.getText().trim()),
                    LocalDate.parse(endDateField.getText().trim()),
                    Integer.parseInt(maxStudentsField.getText().trim())
                );
                
                if (teacherCombo.getSelectedIndex() > 0) {
                    String teacherInfo = (String) teacherCombo.getSelectedItem();
                    String teacherId = teacherInfo.split(" - ")[0];
                    Teacher teacher = teacherService.getTeacherByTeacherId(teacherId);
                    newCourse.setAssignedTeacher(teacher);
                }
                
                if (courseService.addCourse(newCourse)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Course added successfully!\n\n" +
                        "Code: " + newCourse.getCourseCode() + "\n" +
                        "Name: " + newCourse.getCourseName() + "\n" +
                        "Credits: " + newCourse.getCreditHours() + "\n" +
                        "Students: 0/" + newCourse.getMaxStudents(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dialog.dispose();
                    loadCourses();
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void editCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
        Course course = courseService.getCourseByCode(courseCode);
        
        if (course == null) {
            JOptionPane.showMessageDialog(this,
                "Course not found in database.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Course", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField codeField = new JTextField(course.getCourseCode());
        codeField.setEditable(false);
        JTextField nameField = new JTextField(course.getCourseName());
        JTextField creditsField = new JTextField(String.valueOf(course.getCreditHours()));
        JTextField deptField = new JTextField(course.getDepartment());
        JTextArea descArea = new JTextArea(course.getDescription(), 3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JTextField startDateField = new JTextField(course.getStartDate().format(formatter));
        JTextField endDateField = new JTextField(course.getEndDate().format(formatter));
        JTextField maxStudentsField = new JTextField(String.valueOf(course.getMaxStudents()));
        
        JComboBox<String> teacherCombo = new JComboBox<>();
        teacherCombo.addItem("-- Select Teacher --");
        List<Teacher> teachers = teacherService.getAllTeachers();
        for (Teacher teacher : teachers) {
            teacherCombo.addItem(teacher.getTeacherId() + " - " + teacher.getName());
        }
        
        if (course.getAssignedTeacher() != null) {
            String currentTeacher = course.getAssignedTeacher().getTeacherId() + " - " + 
                                   course.getAssignedTeacher().getName();
            for (int i = 0; i < teacherCombo.getItemCount(); i++) {
                if (teacherCombo.getItemAt(i).equals(currentTeacher)) {
                    teacherCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        formPanel.add(new JLabel("Course Code:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Course Name *:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Credit Hours:"));
        formPanel.add(creditsField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Start Date:"));
        formPanel.add(startDateField);
        formPanel.add(new JLabel("End Date:"));
        formPanel.add(endDateField);
        formPanel.add(new JLabel("Max Students:"));
        formPanel.add(maxStudentsField);
        formPanel.add(new JLabel("Assign Teacher:"));
        formPanel.add(teacherCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update Course");
        JButton cancelButton = new JButton("Cancel");
        
        updateButton.setBackground(new Color(241, 196, 15));
        updateButton.setForeground(Color.BLACK);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        
        updateButton.addActionListener(e -> {
            try {
                course.setCourseName(nameField.getText().trim());
                course.setDescription(descArea.getText().trim());
                course.setCreditHours(Integer.parseInt(creditsField.getText().trim()));
                course.setDepartment(deptField.getText().trim());
                course.setStartDate(LocalDate.parse(startDateField.getText().trim()));
                course.setEndDate(LocalDate.parse(endDateField.getText().trim()));
                course.setMaxStudents(Integer.parseInt(maxStudentsField.getText().trim()));
                
                if (teacherCombo.getSelectedIndex() > 0) {
                    String teacherInfo = (String) teacherCombo.getSelectedItem();
                    String teacherId = teacherInfo.split(" - ")[0];
                    Teacher teacher = teacherService.getTeacherByTeacherId(teacherId);
                    course.setAssignedTeacher(teacher);
                } else {
                    course.setAssignedTeacher(null);
                }
                
                if (courseService.updateCourse(course)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Course updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadCourses();
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete course?\n\n" +
            "Code: " + courseCode + "\n" +
            "Name: " + courseName + "\n\n" +
            "This will also delete all student enrollments!\n" +
            "This action cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Course course = courseService.getCourseByCode(courseCode);
            if (course != null) {
                if (courseService.deleteCourse(course.getId())) {
                    JOptionPane.showMessageDialog(this,
                        "Course deleted successfully:\n" +
                        "Code: " + courseCode + "\n" +
                        "Name: " + courseName,
                        "Delete Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete course from database.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void manageCourseStudents() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to manage students.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
        
        System.out.println("\n=== OPENING MANAGE STUDENTS FOR: " + courseCode + " ===");
        
        // الحصول على المادة مع الطلاب
        Course course = getCourseWithEnrollmentsDirect(courseCode);
        if (course == null) {
            JOptionPane.showMessageDialog(this,
                "Course not found in database.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // إنشاء النافذة
        JDialog dialog = new JDialog(this, "Manage Students - " + course.getCourseCode(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 550);
        
        // ========== لوحة المعلومات ==========
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Course Information"));
        
        JLabel courseLabel = new JLabel("Course:");
        JLabel courseValue = new JLabel(course.getCourseCode() + " - " + course.getCourseName());
        JLabel teacherLabel = new JLabel("Teacher:");
        JLabel teacherValue = new JLabel(course.getAssignedTeacher() != null ? 
            course.getAssignedTeacher().getName() : "Not Assigned");
        JLabel enrollmentLabel = new JLabel("Current Enrollment:");
        JLabel enrollmentValue = new JLabel(course.getCurrentEnrollment() + "/" + course.getMaxStudents());
        JLabel availableLabel = new JLabel("Available Seats:");
        JLabel availableValue = new JLabel(String.valueOf(course.getAvailableSeats()));
        
        // جعل النص واضحاً
        courseValue.setFont(new Font("Arial", Font.BOLD, 12));
        enrollmentValue.setFont(new Font("Arial", Font.BOLD, 12));
        enrollmentValue.setForeground(new Color(0, 100, 0));
        
        infoPanel.add(courseLabel);
        infoPanel.add(courseValue);
        infoPanel.add(teacherLabel);
        infoPanel.add(teacherValue);
        infoPanel.add(enrollmentLabel);
        infoPanel.add(enrollmentValue);
        infoPanel.add(availableLabel);
        infoPanel.add(availableValue);
        
        // ========== جدول الطلاب ==========
        String[] columns = {"Student ID", "Name", "Grade", "Email"};
        DefaultTableModel enrolledModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable enrolledTable = new JTable(enrolledModel);
        enrolledTable.setRowHeight(30);
        enrolledTable.setFont(new Font("Arial", Font.PLAIN, 12));
        enrolledTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        enrolledTable.getTableHeader().setBackground(new Color(70, 130, 180));
        enrolledTable.getTableHeader().setForeground(Color.WHITE);
        
        // تحميل البيانات فوراً
        System.out.println("Loading table with " + course.getEnrolledStudents().size() + " students...");
        refreshEnrolledStudentsTable(enrolledModel, course);
        
        JScrollPane enrolledScroll = new JScrollPane(enrolledTable);
        enrolledScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Enrolled Students (" + course.getCurrentEnrollment() + ")",
            0, 0, new Font("Arial", Font.BOLD, 14)
        ));
        
        // ========== أزرار التحكم ==========
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton enrollButton = createStyledButton("➕ Enroll Student", new Color(46, 204, 113));
        JButton unenrollButton = createStyledButton("➖ Remove Student", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("🔄 Refresh List", new Color(52, 152, 219));
        JButton closeButton = createStyledButton("✖ Close", new Color(149, 165, 166));
        
        // إضافة Listeners
        enrollButton.addActionListener(e -> {
            if (course.isFull()) {
                JOptionPane.showMessageDialog(dialog,
                    "Course is full! Cannot enroll more students.\n" +
                    "Maximum capacity: " + course.getMaxStudents(),
                    "Course Full",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // الحصول على الطلاب المتاحين
            List<Student> availableStudents = getAvailableStudentsForCourse(course.getId());
            
            if (availableStudents.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "All students are already enrolled in this course.",
                    "No Students Available",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] studentOptions = new String[availableStudents.size()];
            for (int i = 0; i < availableStudents.size(); i++) {
                Student s = availableStudents.get(i);
                studentOptions[i] = s.getStudentId() + " - " + s.getName() + " (" + s.getGrade() + ")";
            }
            
            String selectedStudent = (String) JOptionPane.showInputDialog(dialog,
                "Select student to enroll:\n" +
                "Available seats: " + course.getAvailableSeats() + "\n" +
                "Current enrollment: " + course.getCurrentEnrollment() + "/" + course.getMaxStudents(),
                "Enroll Student",
                JOptionPane.PLAIN_MESSAGE,
                null,
                studentOptions,
                studentOptions[0]);
            
            if (selectedStudent != null && !selectedStudent.isEmpty()) {
                String studentId = selectedStudent.split(" - ")[0];
                
                try {
                    // تسجيل الطالب
                    boolean success = enrollStudentDirect(course.getId(), studentId);
                    
                    if (success) {
                        // إعادة تحميل البيانات
                        Course updatedCourse = getCourseWithEnrollmentsDirect(courseCode);
                        if (updatedCourse != null) {
                            course.getEnrolledStudents().clear();
                            course.getEnrolledStudents().addAll(updatedCourse.getEnrolledStudents());
                        }
                        
                        // تحديث الواجهة
                        refreshEnrolledStudentsTable(enrolledModel, course);
                        enrollmentValue.setText(course.getCurrentEnrollment() + "/" + course.getMaxStudents());
                        availableValue.setText(String.valueOf(course.getAvailableSeats()));
                        enrolledScroll.setBorder(BorderFactory.createTitledBorder(
                            "Enrolled Students (" + course.getCurrentEnrollment() + ")"
                        ));
                        
                        // تحديث الجدول الرئيسي
                        updateMainCourseTable(courseCode, course.getCurrentEnrollment());
                        
                        JOptionPane.showMessageDialog(dialog,
                            "✅ Student enrolled successfully!\n\n" +
                            "Student: " + selectedStudent.split(" - ")[1] + "\n" +
                            "New enrollment: " + course.getCurrentEnrollment() + "/" + course.getMaxStudents(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "❌ Failed to enroll student.",
                            "Enrollment Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ Error: " + ex.getMessage(),
                        "Enrollment Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        unenrollButton.addActionListener(e -> {
            int selectedStudentRow = enrolledTable.getSelectedRow();
            if (selectedStudentRow == -1) {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a student to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String studentId = (String) enrolledModel.getValueAt(selectedStudentRow, 0);
            String studentName = (String) enrolledModel.getValueAt(selectedStudentRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Remove student from course?\n\n" +
                "Student: " + studentName + "\n" +
                "ID: " + studentId,
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // إزالة الطالب
                    boolean success = unenrollStudentDirect(course.getId(), studentId);
                    
                    if (success) {
                        // إعادة تحميل البيانات
                        Course updatedCourse = getCourseWithEnrollmentsDirect(courseCode);
                        if (updatedCourse != null) {
                            course.getEnrolledStudents().clear();
                            course.getEnrolledStudents().addAll(updatedCourse.getEnrolledStudents());
                        }
                        
                        // تحديث الواجهة
                        refreshEnrolledStudentsTable(enrolledModel, course);
                        enrollmentValue.setText(course.getCurrentEnrollment() + "/" + course.getMaxStudents());
                        availableValue.setText(String.valueOf(course.getAvailableSeats()));
                        enrolledScroll.setBorder(BorderFactory.createTitledBorder(
                            "Enrolled Students (" + course.getCurrentEnrollment() + ")"
                        ));
                        
                        // تحديث الجدول الرئيسي
                        updateMainCourseTable(courseCode, course.getCurrentEnrollment());
                        
                        JOptionPane.showMessageDialog(dialog,
                            "✅ Student removed from course.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "❌ Failed to remove student.",
                            "Removal Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ Error: " + ex.getMessage(),
                        "Removal Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            System.out.println("\n=== MANUAL REFRESH CLICKED ===");
            Course refreshedCourse = getCourseWithEnrollmentsDirect(courseCode);
            if (refreshedCourse != null) {
                course.getEnrolledStudents().clear();
                course.getEnrolledStudents().addAll(refreshedCourse.getEnrolledStudents());
                refreshEnrolledStudentsTable(enrolledModel, course);
                enrollmentValue.setText(course.getCurrentEnrollment() + "/" + course.getMaxStudents());
                availableValue.setText(String.valueOf(course.getAvailableSeats()));
                enrolledScroll.setBorder(BorderFactory.createTitledBorder(
                    "Enrolled Students (" + course.getCurrentEnrollment() + ")"
                ));
                JOptionPane.showMessageDialog(dialog,
                    "✅ Refreshed! Showing " + course.getCurrentEnrollment() + " students.",
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        closeButton.addActionListener(e -> {
            dialog.dispose();
            loadCourses(); // تحديث الجدول الرئيسي
        });
        
        buttonPanel.add(enrollButton);
        buttonPanel.add(unenrollButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        // ========== تجميع النافذة ==========
        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(enrolledScroll, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        System.out.println("=== DIALOG DISPLAYED ===");
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void viewCourseDetails() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to view details.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
        Course course = courseService.getCourseByCode(courseCode);
        
        if (course == null) {
            JOptionPane.showMessageDialog(this,
                "Course not found in database.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Course Details - " + course.getCourseCode(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        
        JTextArea detailsArea = new JTextArea(course.toFullString());
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(52, 152, 219));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void addSampleCourses() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "This will add 10 sample courses with student enrollments.\n" +
            "This is useful for testing the system.\n\n" +
            "Continue?",
            "Add Sample Courses",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            courseService.addSampleCourses();
            loadCourses();
            JOptionPane.showMessageDialog(this,
                "✅ Sample courses added successfully!\n" +
                "You can now test course management features.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void setupFrame() {
        setTitle("Course Management");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    // ==================== الدوال المساعدة ====================
    
    /**
     * الحصول على المادة مع الطلاب
     */
    private Course getCourseWithEnrollmentsDirect(String courseCode) {
        Course course = courseService.getCourseByCode(courseCode);
        if (course != null) {
            // الحصول على الطلاب المسجلين مباشرة
            List<Student> enrolledStudents = getEnrolledStudentsDirect(course.getId());
            
            System.out.println("📥 Adding " + enrolledStudents.size() + " students to course object");
            System.out.println("   Before clear: " + course.getEnrolledStudents().size() + " students");
            
            course.getEnrolledStudents().clear();
            System.out.println("   After clear: " + course.getEnrolledStudents().size() + " students");
            
            course.getEnrolledStudents().addAll(enrolledStudents);
            System.out.println("   After addAll: " + course.getEnrolledStudents().size() + " students");
            
            // التحقق من أن الطلاب مضافون فعلاً
            if (!enrolledStudents.isEmpty()) {
                System.out.println("   Sample student: " + enrolledStudents.get(0).getStudentId() + 
                                  " - " + enrolledStudents.get(0).getName());
            }
        }
        return course;
    }
    
    /**
     * الحصول على الطلاب المسجلين مباشرة من قاعدة البيانات
     */
    private List<Student> getEnrolledStudentsDirect(int courseId) {
        List<Student> students = new ArrayList<>();
        
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            
            // استعلام DEBUG: التحقق أولاً من وجود بيانات
            String debugSql = "SELECT COUNT(*) as count FROM course_enrollments WHERE course_id = ?";
            pstmt = connection.prepareStatement(debugSql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int totalEnrollments = rs.getInt("count");
                System.out.println("🔍 DEBUG: Total enrollments in database for course " + courseId + ": " + totalEnrollments);
            }
            
            rs.close();
            pstmt.close();
            
            // الآن جلب البيانات الكاملة
            String sql = """
                SELECT DISTINCT s.student_id
                FROM course_enrollments ce
                INNER JOIN students s ON ce.student_id = s.student_id
                WHERE ce.course_id = ?
                ORDER BY s.student_id
                """;
            
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            int count = 0;
            boolean studentServiceAvailable = true;
            
            while (rs.next()) {
                count++;
                String studentId = rs.getString("student_id");
                System.out.println("   Student ID found: " + studentId);
                
                // المحاولة الأولى: استخدام studentService
                Student student = null;
                if (studentServiceAvailable) {
                    try {
                        student = studentService.getStudentByStudentId(studentId);
                        if (student == null) {
                            System.err.println("   ❗ studentService returned null for ID: " + studentId);
                        }
                    } catch (Exception e) {
                        System.err.println("   ❌ studentService error for " + studentId + ": " + e.getMessage());
                        studentServiceAvailable = false;
                    }
                }
                
                // المحاولة الثانية: إنشاء Student يدوياً إذا فشلت الخدمة
                if (student == null) {
                    student = createStudentManually(connection, studentId);
                }
                
                if (student != null) {
                    students.add(student);
                    System.out.println("   ✅ Added: " + student.getName() + " (" + studentId + ")");
                } else {
                    System.err.println("   ❌ Could not create Student object for: " + studentId);
                }
            }
            
            System.out.println("✅ FINAL: Successfully loaded " + students.size() + " out of " + count + " enrolled students");
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error in getEnrolledStudentsDirect: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return students;
    }
    
    /**
     * دالة مساعدة لإنشاء Student يدوياً إذا فشلت الخدمة
     */
    private Student createStudentManually(Connection connection, String studentId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = """
                SELECT s.student_id, p.name, p.email, p.phone, p.address, 
                       p.birth_date, s.grade, s.enrollment_date
                FROM students s
                JOIN persons p ON s.person_id = p.id
                WHERE s.student_id = ?
                """;
            
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email") != null ? rs.getString("email") : "";
                String phone = rs.getString("phone") != null ? rs.getString("phone") : "";
                String address = rs.getString("address") != null ? rs.getString("address") : "";
                String grade = rs.getString("grade") != null ? rs.getString("grade") : "";
                
                // التعامل مع التواريخ
                LocalDate birthDate = LocalDate.now();
                LocalDate enrollmentDate = LocalDate.now();
                
                try {
                    String birthDateStr = rs.getString("birth_date");
                    if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                        birthDate = LocalDate.parse(birthDateStr);
                    }
                    
                    String enrollmentDateStr = rs.getString("enrollment_date");
                    if (enrollmentDateStr != null && !enrollmentDateStr.trim().isEmpty()) {
                        enrollmentDate = LocalDate.parse(enrollmentDateStr);
                    }
                } catch (Exception e) {
                    System.err.println("   ⚠️ Date parsing error for " + studentId);
                }
                
                // إنشاء Student باستخدام المنشئ الصحيح
                return new Student(studentId, name, email, phone, birthDate, grade, address, enrollmentDate);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating student manually: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error in Student constructor for " + studentId + ": " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                // تجاهل
            }
        }
        
        return null;
    }
    
    /**
     * الحصول على الطلاب المتاحين للتسجيل في المادة
     */
    private List<Student> getAvailableStudentsForCourse(int courseId) {
        List<Student> allStudents = studentService.getAllStudents();
        List<Student> enrolledStudents = getEnrolledStudentsDirect(courseId);
        List<Student> availableStudents = new ArrayList<>();
        
        for (Student student : allStudents) {
            boolean isEnrolled = false;
            for (Student enrolled : enrolledStudents) {
                if (enrolled.getStudentId().equals(student.getStudentId())) {
                    isEnrolled = true;
                    break;
                }
            }
            if (!isEnrolled) {
                availableStudents.add(student);
            }
        }
        
        return availableStudents;
    }
    
    /**
     * تسجيل طالب مباشرة في قاعدة البيانات
     */
    private boolean enrollStudentDirect(int courseId, String studentId) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO course_enrollments (course_id, student_id) VALUES (?, ?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studentId);
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Direct enrollment: " + studentId + " -> course " + courseId + " (result: " + result + ")");
            return result > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error enrolling student: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }
    
    /**
     * إزالة طالب مباشرة من قاعدة البيانات
     */
    private boolean unenrollStudentDirect(int courseId, String studentId) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            String sql = "DELETE FROM course_enrollments WHERE course_id = ? AND student_id = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studentId);
            
            int result = pstmt.executeUpdate();
            System.out.println("✅ Direct unenrollment: " + studentId + " <- course " + courseId + " (result: " + result + ")");
            return result > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error unenrolling student: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }
    
    /**
     * تحديث جدول الطلاب مع Debug شامل
     */
    private void refreshEnrolledStudentsTable(DefaultTableModel model, Course course) {
        System.out.println("🔄 START refreshEnrolledStudentsTable");
        System.out.println("   Course: " + course.getCourseCode());
        System.out.println("   Enrolled students count from course object: " + course.getEnrolledStudents().size());
        
        // عرض تفاصيل جميع الطلاب
        int studentNum = 1;
        for (Student student : course.getEnrolledStudents()) {
            System.out.println("   Student #" + studentNum + ": " + 
                              student.getStudentId() + " - " + 
                              student.getName() + " (" + student.getGrade() + ")");
            studentNum++;
        }
        
        // مسح الجدول القديم
        model.setRowCount(0);
        System.out.println("   Table cleared, row count: " + model.getRowCount());
        
        // إضافة الصفوف الجديدة
        int addedRows = 0;
        for (Student student : course.getEnrolledStudents()) {
            Object[] rowData = {
                student.getStudentId(),
                student.getName(),
                student.getGrade(),
                student.getEmail()
            };
            
            model.addRow(rowData);
            addedRows++;
            
            System.out.println("   [+] Added row " + addedRows + ": " + 
                              student.getStudentId() + " - " + student.getName());
        }
        
        System.out.println("✅ END refreshEnrolledStudentsTable - Added " + addedRows + " rows");
        System.out.println("   Final table row count: " + model.getRowCount());
        
        // إجبار الجدول على إعادة الرسم
        model.fireTableDataChanged();
    }
    
    /**
     * تحديث الجدول الرئيسي
     */
    private void updateMainCourseTable(String courseCode, int currentEnrollment) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(courseCode)) {
                Course course = courseService.getCourseByCode(courseCode);
                if (course != null) {
                    tableModel.setValueAt(
                        String.format("%d/%d", currentEnrollment, course.getMaxStudents()), 
                        i, 5
                    );
                }
                break;
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CourseManagementFrame frame = new CourseManagementFrame();
            frame.setVisible(true);
        });
    }
}