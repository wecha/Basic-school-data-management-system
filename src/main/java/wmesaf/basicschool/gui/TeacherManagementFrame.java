package wmesaf.basicschool.gui;

import wmesaf.basicschool.business.TeacherService;
import wmesaf.basicschool.business.PersonFactory;
import wmesaf.basicschool.model.Teacher;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherManagementFrame extends JFrame {
    private TeacherService teacherService;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public TeacherManagementFrame() {
        teacherService = new TeacherService();
        initUI();
        setupFrame();
        loadTeachers();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // ========== TOP PANEL ==========
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Teacher Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(155, 89, 182));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(155, 89, 182));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchTeachers());
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        String[] buttonNames = {"Add Teacher", "Edit Teacher", "Delete Teacher", "Refresh"};
        Color[] buttonColors = {
            new Color(46, 204, 113),
            new Color(241, 196, 15),
            new Color(231, 76, 60),
            new Color(155, 89, 182)
        };
        
        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = createActionButton(buttonNames[i], buttonColors[i]);
            buttonPanel.add(button);
        }
        
        // ========== TABLE ==========
        String[] columns = {"Teacher ID", "Name", "Subject", "Salary", "Email", "Hire Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        teacherTable = new JTable(tableModel);
        teacherTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        teacherTable.setRowHeight(30);
        teacherTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        teacherTable.getTableHeader().setBackground(new Color(155, 89, 182));
        teacherTable.getTableHeader().setForeground(Color.WHITE);
        
        teacherTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editTeacher();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        
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
        
        switch (text) {
            case "Add Teacher":
                button.addActionListener(e -> addTeacher());
                break;
            case "Edit Teacher":
                button.addActionListener(e -> editTeacher());
                break;
            case "Delete Teacher":
                button.addActionListener(e -> deleteTeacher());
                break;
            case "Refresh":
                button.addActionListener(e -> loadTeachers());
                break;
        }
        
        return button;
    }
    
    private void loadTeachers() {
        tableModel.setRowCount(0);
        List<Teacher> teachers = teacherService.getAllTeachers();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        System.out.println("📊 Loading " + teachers.size() + " teachers...");
        
        for (Teacher teacher : teachers) {
            Object[] row = {
                teacher.getTeacherId(),
                teacher.getName(),
                teacher.getSubject(),
                String.format("$%.2f", teacher.getSalary()),
                teacher.getEmail(),
                teacher.getHireDate().format(formatter)
            };
            tableModel.addRow(row);
        }
        
        searchField.setText("");
    }
    
    private void searchTeachers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadTeachers();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Teacher> teachers = teacherService.searchTeachersByName(searchTerm);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No teachers found with name containing: " + searchTerm,
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("🔍 Found " + teachers.size() + " teachers matching: " + searchTerm);
        }
        
        for (Teacher teacher : teachers) {
            Object[] row = {
                teacher.getTeacherId(),
                teacher.getName(),
                teacher.getSubject(),
                String.format("$%.2f", teacher.getSalary()),
                teacher.getEmail(),
                teacher.getHireDate().format(formatter)
            };
            tableModel.addRow(row);
        }
    }
    
    private void addTeacher() {
        JDialog dialog = new JDialog(this, "Add New Teacher", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField();
        JTextField teacherIdField = new JTextField();
        JTextField subjectField = new JTextField("Mathematics");
        JTextField salaryField = new JTextField("2500.00");
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField birthDateField = new JTextField("1980-01-01");
        JTextField hireDateField = new JTextField(LocalDate.now().toString());
        
        formPanel.add(new JLabel("Full Name *:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Teacher ID *:"));
        formPanel.add(teacherIdField);
        formPanel.add(new JLabel("Subject:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Salary ($):"));
        formPanel.add(salaryField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        formPanel.add(birthDateField);
        formPanel.add(new JLabel("Hire Date (YYYY-MM-DD):"));
        formPanel.add(hireDateField);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel("* Required fields"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save Teacher");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        
        // ✅ هذا هو الجزء المصحح - استخدام try-catch منفصل
        saveButton.addActionListener(e -> {
            // التحقق من الحقول المطلوبة
            if (nameField.getText().trim().isEmpty() || teacherIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields (Name and Teacher ID)",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // التحقق من وجود ID مكرر
            if (teacherService.teacherIdExists(teacherIdField.getText().trim())) {
                JOptionPane.showMessageDialog(dialog,
                    "Teacher ID already exists. Please use a different ID.",
                    "Duplicate ID",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // محاولة تحويل الراتب
            double salary;
            try {
                salary = Double.parseDouble(salaryField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid salary format. Please enter a valid number.\nExample: 2500.00",
                    "Salary Format Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // محاولة تحويل التواريخ
            LocalDate birthDate;
            LocalDate hireDate;
            try {
                birthDate = LocalDate.parse(birthDateField.getText().trim());
                hireDate = LocalDate.parse(hireDateField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid date format. Please use YYYY-MM-DD format.\nExample: 1980-01-01",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // الآن إنشاء وحفظ المعلم
            try {
                Teacher newTeacher = PersonFactory.createTeacher(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    birthDate,
                    teacherIdField.getText().trim(),
                    subjectField.getText().trim(),
                    salary,
                    hireDate
                );
                
                if (teacherService.addTeacher(newTeacher)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Teacher added successfully!\n\n" +
                        "Name: " + newTeacher.getName() + "\n" +
                        "Teacher ID: " + newTeacher.getTeacherId() + "\n" +
                        "Subject: " + newTeacher.getSubject() + "\n" +
                        "Salary: $" + String.format("%.2f", newTeacher.getSalary()),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dialog.dispose();
                    loadTeachers();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to add teacher. Please try again.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Business Rule Error: " + ex.getMessage(),
                    "Validation Failed",
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
    
    private void editTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a teacher to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String teacherId = (String) tableModel.getValueAt(selectedRow, 0);
        Teacher teacher = teacherService.getTeacherByTeacherId(teacherId);
        
        if (teacher == null) {
            JOptionPane.showMessageDialog(this,
                "Teacher not found in database.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Teacher", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField(teacher.getName());
        JTextField teacherIdField = new JTextField(teacher.getTeacherId());
        teacherIdField.setEditable(false);
        JTextField subjectField = new JTextField(teacher.getSubject());
        JTextField salaryField = new JTextField(String.format("%.2f", teacher.getSalary()));
        JTextField emailField = new JTextField(teacher.getEmail());
        JTextField phoneField = new JTextField(teacher.getPhone());
        JTextField addressField = new JTextField(teacher.getAddress());
        JTextField birthDateField = new JTextField(teacher.getBirthDate().toString());
        JTextField hireDateField = new JTextField(teacher.getHireDate().toString());
        
        formPanel.add(new JLabel("Full Name *:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Teacher ID:"));
        formPanel.add(teacherIdField);
        formPanel.add(new JLabel("Subject:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Salary ($):"));
        formPanel.add(salaryField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Birth Date:"));
        formPanel.add(birthDateField);
        formPanel.add(new JLabel("Hire Date:"));
        formPanel.add(hireDateField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(241, 196, 15));
        saveButton.setForeground(Color.BLACK);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        
        saveButton.addActionListener(e -> {
            try {
                teacher.setName(nameField.getText().trim());
                teacher.setSubject(subjectField.getText().trim());
                teacher.setSalary(Double.parseDouble(salaryField.getText().trim()));
                teacher.setEmail(emailField.getText().trim());
                teacher.setPhone(phoneField.getText().trim());
                teacher.setAddress(addressField.getText().trim());
                teacher.setBirthDate(LocalDate.parse(birthDateField.getText().trim()));
                teacher.setHireDate(LocalDate.parse(hireDateField.getText().trim()));
                
                if (teacherService.updateTeacher(teacher)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Teacher updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTeachers();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to update teacher.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
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
    
    private void deleteTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a teacher to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String teacherId = (String) tableModel.getValueAt(selectedRow, 0);
        String teacherName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete teacher?\n\n" +
            "Name: " + teacherName + "\n" +
            "Teacher ID: " + teacherId + "\n\n" +
            "This action cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Teacher teacher = teacherService.getTeacherByTeacherId(teacherId);
                if (teacher != null) {
                    if (teacherService.deleteTeacher(teacher.getId())) {
                        JOptionPane.showMessageDialog(this,
                            "Teacher deleted successfully:\n" +
                            "Name: " + teacherName + "\n" +
                            "ID: " + teacherId,
                            "Delete Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadTeachers();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to delete teacher from database.",
                            "Delete Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting teacher: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupFrame() {
        setTitle("Teacher Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TeacherManagementFrame frame = new TeacherManagementFrame();
            frame.setVisible(true);
        });
    }
}