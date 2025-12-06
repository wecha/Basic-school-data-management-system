package wmesaf.basicschool.gui;

import javax.swing.*;
import java.awt.*;

public class CourseManagementFrame extends JFrame {
    public CourseManagementFrame() {
        initUI();
        setupFrame();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Course Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(241, 196, 15));
        
        JTextArea contentArea = new JTextArea();
        contentArea.setText("COURSE MANAGEMENT SYSTEM\n\n" +
                           "This module will be implemented in Sprint 3.\n\n" +
                           "Planned features:\n" +
                           "• Create and manage courses\n" +
                           "• Assign teachers to courses\n" +
                           "• Enroll students in courses\n" +
                           "• Track course schedules\n" +
                           "• Generate course reports");
        contentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        contentArea.setEditable(false);
        
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(contentArea), BorderLayout.CENTER);
    }
    
    private void setupFrame() {
        setTitle("Course Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}