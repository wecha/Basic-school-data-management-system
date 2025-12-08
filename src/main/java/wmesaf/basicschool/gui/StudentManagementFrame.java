package wmesaf.basicschool.gui;

import wmesaf.basicschool.business.StudentService;
import wmesaf.basicschool.business.PersonFactory;
import wmesaf.basicschool.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StudentManagementFrame extends JFrame {
    private StudentService studentService;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public StudentManagementFrame() {
        studentService = new StudentService();
        initUI();
        setupFrame();
        loadStudents();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // ========== TOP PANEL ==========
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchStudents());
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        String[] buttonNames = {"Add Student", "Edit Student", "Delete Student", "Refresh"};
        Color[] buttonColors = {
            new Color(46, 204, 113),  // Green
            new Color(241, 196, 15),  // Yellow
            new Color(231, 76, 60),   // Red
            new Color(52, 152, 219)   // Blue
        };
        
        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = createActionButton(buttonNames[i], buttonColors[i]);
            buttonPanel.add(button);
        }
        
        // ========== TABLE ==========
        String[] columns = {"Student ID", "Name", "Grade", "Email", "Phone", "Enrollment Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setRowHeight(30);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentTable.getTableHeader().setBackground(new Color(52, 152, 219));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        
        // Add double-click listener for editing
        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStudent();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
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
        button.setPreferredSize(new Dimension(150, 40));
        
        // Add action listeners
        switch (text) {
            case "Add Student":
                button.addActionListener(e -> addStudent());
                break;
            case "Edit Student":
                button.addActionListener(e -> editStudent());
                break;
            case "Delete Student":
                button.addActionListener(e -> deleteStudent());
                break;
            case "Refresh":
                button.addActionListener(e -> loadStudents());
                break;
        }
        
        return button;
    }
    
    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> students = studentService.getAllStudents();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        System.out.println("📊 Loading " + students.size() + " students...");
        
        for (Student student : students) {
            Object[] row = {
                student.getStudentId(),
                student.getName(),
                student.getGrade(),
                student.getEmail(),
                student.getPhone(),
                student.getEnrollmentDate().format(formatter)
            };
            tableModel.addRow(row);
        }
        
        searchField.setText("");
    }
    
    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStudents();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Student> students = studentService.searchStudentsByName(searchTerm);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No students found with name containing: " + searchTerm,
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("🔍 Found " + students.size() + " students matching: " + searchTerm);
        }
        
        for (Student student : students) {
            Object[] row = {
                student.getStudentId(),
                student.getName(),
                student.getGrade(),
                student.getEmail(),
                student.getPhone(),
                student.getEnrollmentDate().format(formatter)
            };
            tableModel.addRow(row);
        }
    }
    
    private void addStudent() {
        JDialog dialog = new JDialog(this, "Add New Student", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField();
        JTextField studentIdField = new JTextField();
        JTextField gradeField = new JTextField("10th Grade");
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField birthDateField = new JTextField("2005-01-01");
        JTextField enrollDateField = new JTextField(LocalDate.now().toString());
        
        formPanel.add(new JLabel("Full Name *:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Student ID *:"));
        formPanel.add(studentIdField);
        formPanel.add(new JLabel("Grade/Class:"));
        formPanel.add(gradeField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        formPanel.add(birthDateField);
        formPanel.add(new JLabel("Enrollment Date (YYYY-MM-DD):"));
        formPanel.add(enrollDateField);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel("* Required fields"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save Student");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty() || studentIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields (Name and Student ID)",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                if (studentService.studentIdExists(studentIdField.getText().trim())) {
                    JOptionPane.showMessageDialog(dialog,
                        "Student ID already exists. Please use a different ID.",
                        "Duplicate ID",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // استخدام Factory Pattern لإنشاء الطالب
                Student newStudent = PersonFactory.createStudent(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    LocalDate.parse(birthDateField.getText().trim()),
                    studentIdField.getText().trim(),
                    gradeField.getText().trim(),
                    LocalDate.parse(enrollDateField.getText().trim())
                );
                
                // استخدام Service Layer لحفظ الطالب
                if (studentService.addStudent(newStudent)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Student added successfully!\n\n" +
                        "Name: " + newStudent.getName() + "\n" +
                        "Student ID: " + newStudent.getStudentId() + "\n" +
                        "Grade: " + newStudent.getGrade(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dialog.dispose();
                    loadStudents();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to add student. Please try again.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Business Rule Error: " + ex.getMessage(),
                    "Validation Failed",
                    JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid date format. Please use YYYY-MM-DD format.\n" +
                    "Example: 2005-03-15",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
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
    
    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);
        
        Student student = studentService.getStudentByStudentId(studentId);
        
        if (student == null) {
            JOptionPane.showMessageDialog(this,
                "Student not found in database.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Student", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField(student.getName());
        JTextField studentIdField = new JTextField(student.getStudentId());
        studentIdField.setEditable(false);
        JTextField gradeField = new JTextField(student.getGrade());
        JTextField emailField = new JTextField(student.getEmail());
        JTextField phoneField = new JTextField(student.getPhone());
        JTextField addressField = new JTextField(student.getAddress());
        JTextField birthDateField = new JTextField(student.getBirthDate().toString());
        JTextField enrollDateField = new JTextField(student.getEnrollmentDate().toString());
        
        formPanel.add(new JLabel("Full Name *:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(studentIdField);
        formPanel.add(new JLabel("Grade/Class:"));
        formPanel.add(gradeField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        formPanel.add(birthDateField);
        formPanel.add(new JLabel("Enrollment Date (YYYY-MM-DD):"));
        formPanel.add(enrollDateField);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel("* Student ID cannot be changed"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update Student");
        JButton cancelButton = new JButton("Cancel");
        
        updateButton.setBackground(new Color(241, 196, 15));
        updateButton.setForeground(Color.BLACK);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        
        updateButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Student name cannot be empty",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                student.setName(nameField.getText().trim());
                student.setGrade(gradeField.getText().trim());
                student.setEmail(emailField.getText().trim());
                student.setPhone(phoneField.getText().trim());
                student.setAddress(addressField.getText().trim());
                student.setBirthDate(LocalDate.parse(birthDateField.getText().trim()));
                student.setEnrollmentDate(LocalDate.parse(enrollDateField.getText().trim()));
                
                if (studentService.updateStudent(student)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Student updated successfully!\n\n" +
                        "Name: " + student.getName() + "\n" +
                        "Student ID: " + student.getStudentId() + "\n" +
                        "Grade: " + student.getGrade(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dialog.dispose();
                    loadStudents();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to update student. Please try again.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid date format. Please use YYYY-MM-DD format.\n" +
                    "Example: 2005-03-15",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
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
    
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete student?\n\n" +
            "Name: " + studentName + "\n" +
            "Student ID: " + studentId + "\n\n" +
            "This action cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Student student = studentService.getStudentByStudentId(studentId);
                if (student != null) {
                    if (studentService.deleteStudent(student.getId())) {
                        JOptionPane.showMessageDialog(this,
                            "Student deleted successfully:\n" +
                            "Name: " + studentName + "\n" +
                            "ID: " + studentId,
                            "Delete Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadStudents();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to delete student from database.",
                            "Delete Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting student: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupFrame() {
        setTitle("Student Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagementFrame frame = new StudentManagementFrame();
            frame.setVisible(true);
        });
    }
}