//package wmesaf.basicschool.gui;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class CourseManagementFrame extends JFrame {
//    public CourseManagementFrame() {
//        initUI();
//        setupFrame();
//    }
//    
//    private void initUI() {
//        setLayout(new BorderLayout());
//        
//        JLabel titleLabel = new JLabel("Course Management", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
//        titleLabel.setForeground(new Color(241, 196, 15));
//        
//        JTextArea contentArea = new JTextArea();
//        contentArea.setText("COURSE MANAGEMENT SYSTEM\n\n" +
//                           "This module will be implemented in the future.\n\n" +
//                           "Planned features:\n" +
//                           "• Create and manage courses\n" +
//                           "• Assign teachers to courses\n" +
//                           "• Enroll students in courses\n" +
//                           "• Track course schedules\n" +
//                           "• Generate course reports");
//        contentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
//        contentArea.setEditable(false);
//        
//        add(titleLabel, BorderLayout.NORTH);
//        add(new JScrollPane(contentArea), BorderLayout.CENTER);
//    }
//    
//    private void setupFrame() {
//        setTitle("Course Management");
//        setSize(600, 400);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
//    }
//}
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
        titleLabel.setForeground(new Color(142, 68, 173)); // لون أرجواني للمواد
        
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
            new Color(46, 204, 113),  // أخضر - إضافة
            new Color(241, 196, 15),  // أصفر - تعديل
            new Color(231, 76, 60),   // أحمر - حذف
            new Color(52, 152, 219),  // أزرق - إدارة طلاب
            new Color(155, 89, 182),  // أرجواني - تفاصيل
            new Color(149, 165, 166)  // رمادي - تحديث
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
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class; // Credits
                if (columnIndex == 5) return String.class;  // Students format
                return String.class;
            }
        };
        
        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseTable.getTableHeader().setBackground(new Color(142, 68, 173));
        courseTable.getTableHeader().setForeground(Color.WHITE);
        
        // جعل عرض الأعمدة مناسباً
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Code
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Credits
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Department
        courseTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Teacher
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Students
        courseTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Start Date
        courseTable.getColumnModel().getColumn(7).setPreferredWidth(100); // End Date
        
        // إضافة مستمع للنقر المزدوج
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
        
        // إضافة مستمعات الأحداث
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
        
        System.out.println("📚 Loading " + courses.size() + " courses...");
        
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
        
        // إخفاء زر العينات إذا كان هناك مواد
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
        } else {
            System.out.println("🔍 Found " + courses.size() + " courses matching: " + searchTerm);
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
        
        // قائمة المعلمين
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
                // التحقق من المدخلات
                if (codeField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please fill in required fields (Course Code and Name)",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // التحقق من كود المادة
                if (courseService.courseCodeExists(codeField.getText().trim())) {
                    JOptionPane.showMessageDialog(dialog,
                        "Course code already exists. Please use a different code.",
                        "Duplicate Code",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // إنشاء المادة
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
                
                // تعيين المعلم إذا تم اختياره
                if (teacherCombo.getSelectedIndex() > 0) {
                    String teacherInfo = (String) teacherCombo.getSelectedItem();
                    String teacherId = teacherInfo.split(" - ")[0];
                    Teacher teacher = teacherService.getTeacherByTeacherId(teacherId);
                    newCourse.setAssignedTeacher(teacher);
                }
                
                // حفظ المادة
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
        codeField.setEditable(false); // لا يمكن تغيير الكود
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
        
        // قائمة المعلمين
        JComboBox<String> teacherCombo = new JComboBox<>();
        teacherCombo.addItem("-- Select Teacher --");
        List<Teacher> teachers = teacherService.getAllTeachers();
        for (Teacher teacher : teachers) {
            teacherCombo.addItem(teacher.getTeacherId() + " - " + teacher.getName());
        }
        
        // اختيار المعلم الحالي
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
                
                // تحديث المعلم
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
        Course course = courseService.getCourseByCode(courseCode);
        
        if (course == null) {
            JOptionPane.showMessageDialog(this,
                "Course not found in database.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Manage Students - " + course.getCourseCode(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        
        // لوحة معلومات المادة
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Course Information"));
        infoPanel.add(new JLabel("Course:"));
        infoPanel.add(new JLabel(course.getCourseCode() + " - " + course.getCourseName()));
        infoPanel.add(new JLabel("Teacher:"));
        infoPanel.add(new JLabel(course.getAssignedTeacher() != null ? 
            course.getAssignedTeacher().getName() : "Not Assigned"));
        infoPanel.add(new JLabel("Enrollment:"));
        infoPanel.add(new JLabel(course.getCurrentEnrollment() + "/" + course.getMaxStudents() + 
            " (" + course.getAvailableSeats() + " available)"));
        
        // قائمة الطلاب المسجلين
        DefaultTableModel enrolledModel = new DefaultTableModel(
            new String[]{"Student ID", "Name", "Grade", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable enrolledTable = new JTable(enrolledModel);
        List<Student> enrolledStudents = course.getEnrolledStudents();
        for (Student student : enrolledStudents) {
            enrolledModel.addRow(new Object[]{
                student.getStudentId(),
                student.getName(),
                student.getGrade(),
                student.getEmail()
            });
        }
        
        JScrollPane enrolledScroll = new JScrollPane(enrolledTable);
        enrolledScroll.setBorder(BorderFactory.createTitledBorder("Enrolled Students"));
        
        // لوحة الإجراءات
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton enrollButton = new JButton("➕ Enroll Student");
        JButton unenrollButton = new JButton("➖ Remove Student");
        
        enrollButton.setBackground(new Color(46, 204, 113));
        enrollButton.setForeground(Color.WHITE);
        unenrollButton.setBackground(new Color(231, 76, 60));
        unenrollButton.setForeground(Color.WHITE);
        
        enrollButton.addActionListener(e -> {
            // عرض قائمة الطلاب غير المسجلين
            List<Student> allStudents = studentService.getAllStudents();
            allStudents.removeAll(enrolledStudents);
            
            if (allStudents.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "All students are already enrolled in this course.",
                    "No Students Available",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (course.isFull()) {
                JOptionPane.showMessageDialog(dialog,
                    "Course is full! Cannot enroll more students.",
                    "Course Full",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String[] studentOptions = allStudents.stream()
                .map(s -> s.getStudentId() + " - " + s.getName() + " (" + s.getGrade() + ")")
                .toArray(String[]::new);
            
            String selected = (String) JOptionPane.showInputDialog(dialog,
                "Select student to enroll:\nAvailable seats: " + course.getAvailableSeats(),
                "Enroll Student",
                JOptionPane.PLAIN_MESSAGE,
                null,
                studentOptions,
                studentOptions[0]);
            
            if (selected != null) {
                String studentId = selected.split(" - ")[0];
                try {
                    if (courseService.enrollStudentInCourse(course.getId(), studentId)) {
                        JOptionPane.showMessageDialog(dialog,
                            "Student enrolled successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        manageCourseStudents(); // إعادة تحميل
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
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
                if (courseService.unenrollStudentFromCourse(course.getId(), studentId)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Student removed from course.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    manageCourseStudents(); // إعادة تحميل
                }
            }
        });
        
        actionPanel.add(enrollButton);
        actionPanel.add(unenrollButton);
        
        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(enrolledScroll, BorderLayout.CENTER);
        dialog.add(actionPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
                "Sample courses added successfully!\n" +
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CourseManagementFrame frame = new CourseManagementFrame();
            frame.setVisible(true);
        });
    }
}