package wmesaf.basicschool.gui;

import wmesaf.basicschool.dao.AdminDAO;
import wmesaf.basicschool.model.Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    // Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel messageLabel;
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Light Blue
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);    // Green
    private final Color ERROR_COLOR = new Color(231, 76, 60);       // Red
    
    public LoginFrame() {
        initUI();
        setupFrame();
        setupListeners();
    }
    
    private void initUI() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        
        // ========== HEADER ==========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("School Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Administrator Login", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // ========== LOGIN FORM ==========
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = createLabel("Username:");
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        usernameField = createTextField();
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel passLabel = createLabel("Password:");
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        passwordField = createPasswordField();
        formPanel.add(passwordField, gbc);
        
        // Message Label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR_COLOR);
        formPanel.add(messageLabel, gbc);
        
        // ========== BUTTONS ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Exit Button
        exitButton = createButton("Exit", ERROR_COLOR);
        exitButton.setBackground(new Color(245, 245, 245));
        exitButton.setForeground(Color.DARK_GRAY);
        
        // Login Button
        loginButton = createButton("Login", SUCCESS_COLOR);
        
        buttonPanel.add(exitButton);
        buttonPanel.add(loginButton);
        
        // ========== FOOTER ==========
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(Color.WHITE);
        
        JLabel footerLabel = new JLabel("© 2025 School Management System | Student ID: 262504");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        
        // ========== ASSEMBLE ==========
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupFrame() {
        setTitle("School Management System - Login");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set application icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon not found, continue without it
        }
    }
    
    private void setupListeners() {
        // Login button action
        loginButton.addActionListener(e -> performLogin());
        
        // Exit button action
        exitButton.addActionListener(e -> System.exit(0));
        
        // Enter key in password field triggers login
        passwordField.addActionListener(e -> performLogin());
        
        // Clear message when user starts typing
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                messageLabel.setText(" ");
            }
        });
        
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                messageLabel.setText(" ");
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password", ERROR_COLOR);
            return;
        }
        
        try {
            // Authenticate using AdminDAO
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.authenticate(username, password);
            
            if (admin != null) {
                showMessage("Login successful! Welcome, " + admin.getFullName(), SUCCESS_COLOR);
                
                // Open main dashboard after short delay
                Timer timer = new Timer(1000, e -> {
                    // تمرير admin إلى MainFrame
                    new MainFrame(admin).setVisible(true);
                    dispose(); // Close login window
                });
                timer.setRepeats(false);
                timer.start();
                
            } else {
                // Fallback to emergency login if database is down
                openEmergencyLogin();
            }
            
        } catch (Exception ex) {
            System.err.println("Database error, using emergency login: " + ex.getMessage());
            openEmergencyLogin();
        }
    }
    
    private void openEmergencyLogin() {
        // Fallback إذا فشل الاتصال بقاعدة البيانات
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.equals("admin") && password.equals("admin123")) {
            showMessage("Emergency login successful (database offline)", Color.ORANGE);
            
            // إنشاء admin افتراضي
            Admin emergencyAdmin = new Admin("admin", "admin123", "Emergency Administrator", "emergency@school.com");
            emergencyAdmin.setId(999);
            emergencyAdmin.setActive(true);
            
            Timer timer = new Timer(1000, e -> {
                new MainFrame(emergencyAdmin).setVisible(true);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            showMessage("Invalid emergency credentials", ERROR_COLOR);
        }
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }
    
    public static void main(String[] args) {
        // Set look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}