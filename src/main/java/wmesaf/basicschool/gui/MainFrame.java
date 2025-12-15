package wmesaf.basicschool.gui;

import wmesaf.basicschool.model.Admin;
import wmesaf.basicschool.dao.StudentDAO;
import wmesaf.basicschool.dao.TeacherDAO;
import wmesaf.basicschool.business.DashboardService;
import wmesaf.basicschool.business.ReportService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class MainFrame extends JFrame {
    private Admin currentAdmin;
    private JLabel welcomeLabel;
    private JLabel userInfoLabel;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private DashboardService dashboardService;
    private ReportService reportService;
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SIDEBAR_COLOR = new Color(52, 73, 94);
    private final Color CONTENT_COLOR = new Color(236, 240, 241);
    
    // إحصائيات حقيقية
    private int totalStudents = 0;
    private int totalTeachers = 0;
    private double totalSalary = 0;
    private String systemStatus = "Unknown";
    private String databaseStatus = "Unknown";
    private double uptime = 0;
    
    public MainFrame(Admin admin) {
        this.currentAdmin = admin;
        this.studentDAO = new StudentDAO();
        this.teacherDAO = new TeacherDAO();
        this.dashboardService = new DashboardService();
        this.reportService = new ReportService();
        loadRealStatistics();
        initUI();
        setupFrame();
    }
    
    private void loadRealStatistics() {
        // ✅ استخدام DashboardService للحصول على الإحصائيات
        Map<String, Object> stats = dashboardService.getDashboardStatistics();
        
        totalStudents = (int) stats.get("totalStudents");
        totalTeachers = (int) stats.get("totalTeachers");
        totalSalary = (double) stats.get("totalSalary");
        systemStatus = (String) stats.get("systemStatus");
        databaseStatus = (String) stats.get("databaseStatus");
        
        System.out.println("📊 Real Statistics Loaded from DashboardService:");
        System.out.println("   Students: " + totalStudents);
        System.out.println("   Teachers: " + totalTeachers);
        System.out.println("   Total Salary: $" + totalSalary);
        System.out.println("   System Status: " + systemStatus);
        System.out.println("   Database Status: " + databaseStatus);
    }
    
    private void initUI() {
        // Main container with BorderLayout
        setLayout(new BorderLayout());
        
        // ========== TOP BAR ==========
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setPreferredSize(new Dimension(getWidth(), 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Welcome message
        welcomeLabel = new JLabel("School Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        
        // User info
        String adminName = (currentAdmin != null) ? currentAdmin.getFullName() : "System Administrator";
        userInfoLabel = new JLabel("Admin: " + adminName);
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userInfoLabel.setForeground(new Color(230, 230, 230));
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(userInfoLabel, BorderLayout.CENTER);
        topBar.add(logoutButton, BorderLayout.EAST);
        
        // ========== SIDEBAR ==========
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Sidebar title
        JLabel sidebarTitle = new JLabel("MAIN MENU");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarTitle.setForeground(new Color(200, 200, 200));
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        sidebar.add(sidebarTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Menu buttons
        String[] menuItems = {
            "📊 Dashboard",
            "👨‍🎓 Student Management",
            "👩‍🏫 Teacher Management",
            "📚 Course Management",
            "📈 Reports",
            "⚙️ Settings"
        };
        
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(menuButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // ========== CONTENT PANEL ==========
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CONTENT_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome card
        String welcomeMessage = (currentAdmin != null) ? 
            "Welcome to School Management System\n\nYou are logged in as: " + currentAdmin.getFullName() + 
            "\n\nReal-time Statistics:\n" +
            "• Students: " + totalStudents + "\n" +
            "• Teachers: " + totalTeachers + "\n" +
            "• Total Salary Expense: $" + String.format("%,.2f", totalSalary) + "\n" +
            "• System Status: " + systemStatus + "\n" +
            "• Database: " + databaseStatus :
            "Welcome to School Management System\n\nYou are logged in as: System Administrator" +
            "\n\nUse the sidebar menu to navigate through different sections.";
        
        JPanel welcomeCard = createCard("📊 School Management Dashboard",
            welcomeMessage + "\n\nLast Updated: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Statistics panel مع إحصائيات حقيقية
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(CONTENT_COLOR);
        
        String[] stats = {"Total Students", "Total Teachers", "Total Salary", "System Status"};
        String[] values = {
            String.valueOf(totalStudents),
            String.valueOf(totalTeachers),
            "$" + String.format("%,.2f", totalSalary),
            systemStatus + " / " + databaseStatus
        };
        Color[] colors = {
            new Color(52, 152, 219),    // Blue
            new Color(155, 89, 182),    // Purple
            new Color(46, 204, 113),    // Green
            new Color(241, 196, 15)     // Yellow
        };
        
        for (int i = 0; i < stats.length; i++) {
            statsPanel.add(createStatCard(stats[i], values[i], colors[i]));
        }
        
        // زر تحديث Dashboard
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("🔄 Refresh Dashboard");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshDashboard());
        refreshPanel.add(refreshButton);
        
        contentPanel.add(welcomeCard, BorderLayout.NORTH);
        contentPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(refreshPanel, BorderLayout.SOUTH);
        
        // ========== ASSEMBLE ==========
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(70, 90, 110));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        button.setMaximumSize(new Dimension(200, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(86, 101, 115));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 90, 110));
            }
        });
        
        // Action listener
        button.addActionListener(e -> handleMenuAction(text));
        
        return button;
    }
    
    private void handleMenuAction(String menuItem) {
        System.out.println("Menu clicked: " + menuItem);
        
        switch (menuItem) {
            case "📊 Dashboard":
                refreshDashboard();
                break;
            case "👨‍🎓 Student Management":
                openStudentManagement();
                break;
            case "👩‍🏫 Teacher Management":
                openTeacherManagement();
                break;
            case "📚 Course Management":
                openCourseManagement();
                break;
            case "📈 Reports":
                showEnhancedReports();
                break;
            case "⚙️ Settings":
                showSettings();
                break;
            default:
                System.out.println("Unknown menu item: " + menuItem);
        }
    }
    
    private void refreshDashboard() {
        // تحديث الإحصائيات
        loadRealStatistics();
        
        // إعادة إنشاء واجهة Dashboard
        getContentPane().removeAll();
        initUI();
        revalidate();
        repaint();
        
        System.out.println("🔄 Dashboard refreshed with real statistics");
        
        JOptionPane.showMessageDialog(this,
            "Dashboard refreshed with real-time data!\n\n" +
            "Updated Statistics:\n" +
            "• Students: " + totalStudents + "\n" +
            "• Teachers: " + totalTeachers + "\n" +
            "• Total Salary: $" + String.format("%,.2f", totalSalary) + "\n" +
            "• System: " + systemStatus + "\n" +
            "• Database: " + databaseStatus,
            "Dashboard Updated",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openStudentManagement() {
        SwingUtilities.invokeLater(() -> {
            try {
                StudentManagementFrame studentFrame = new StudentManagementFrame();
                studentFrame.setVisible(true);
                System.out.println("✅ Student Management opened successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error opening Student Management:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void openTeacherManagement() {
        SwingUtilities.invokeLater(() -> {
            try {
                TeacherManagementFrame teacherFrame = new TeacherManagementFrame();
                teacherFrame.setVisible(true);
                System.out.println("✅ Teacher Management opened successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error opening Teacher Management:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
// وإضافة دالة openCourseManagement():
private void openCourseManagement() {
    SwingUtilities.invokeLater(() -> {
        try {
            CourseManagementFrame courseFrame = new CourseManagementFrame();
            courseFrame.setVisible(true);
            System.out.println("✅ Course Management opened successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error opening Course Management:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    });
}
//    private void openCourseManagement() {
//        SwingUtilities.invokeLater(() -> {
//            JOptionPane.showMessageDialog(this,
//                "Course Management Module\n\n" +
//                "This module is planned for Sprint 3.\n" +
//                "It will include:\n" +
//                "• Course creation and management\n" +
//                "• Teacher assignment to courses\n" +
//                "• Student enrollment\n" +
//                "• Course scheduling\n" +
//                "• Grade tracking",
//                "Course Management - Coming Soon",
//                JOptionPane.INFORMATION_MESSAGE);
//        });
//    }
    
    /**
     * ✅ الدالة المعدلة: عرض التقارير المحسنة
     */
    private void showEnhancedReports() {
        try {
            // ✅ استخدام ReportService الجديد
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // التقرير 1: Student Statistics
            String studentReport = reportService.generateStudentStatisticsReport();
            JTextArea studentReportArea = new JTextArea(studentReport);
            studentReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            studentReportArea.setEditable(false);
            JScrollPane studentScroll = new JScrollPane(studentReportArea);
            tabbedPane.addTab("Student Statistics", studentScroll);
            
            // التقرير 2: Teacher Statistics
            String teacherReport = reportService.generateTeacherStatisticsReport();
            JTextArea teacherReportArea = new JTextArea(teacherReport);
            teacherReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            teacherReportArea.setEditable(false);
            JScrollPane teacherScroll = new JScrollPane(teacherReportArea);
            tabbedPane.addTab("Teacher Statistics", teacherScroll);
            
            // التقرير 3: System Summary
            String systemReport = reportService.generateSystemSummaryReport();
            JTextArea systemReportArea = new JTextArea(systemReport);
            systemReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            systemReportArea.setEditable(false);
            JScrollPane systemScroll = new JScrollPane(systemReportArea);
            tabbedPane.addTab("System Summary", systemScroll);
            
            // زر التصدير
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton exportButton = new JButton("Export Selected Report");
            exportButton.addActionListener(e -> {
                int selectedTab = tabbedPane.getSelectedIndex();
                String report = "";
                String filename = "";
                
                switch (selectedTab) {
                    case 0:
                        report = reportService.generateStudentStatisticsReport();
                        filename = "student_report_" + java.time.LocalDate.now() + ".txt";
                        break;
                    case 1:
                        report = reportService.generateTeacherStatisticsReport();
                        filename = "teacher_report_" + java.time.LocalDate.now() + ".txt";
                        break;
                    case 2:
                        report = reportService.generateSystemSummaryReport();
                        filename = "system_report_" + java.time.LocalDate.now() + ".txt";
                        break;
                }
                
                try {
                    java.nio.file.Files.write(
                        java.nio.file.Paths.get(filename),
                        report.getBytes()
                    );
                    JOptionPane.showMessageDialog(this,
                        "Report exported successfully to:\n" + filename,
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error exporting report: " + ex.getMessage(),
                        "Export Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            buttonPanel.add(exportButton);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(tabbedPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JOptionPane.showMessageDialog(this, mainPanel, "System Reports", 
                JOptionPane.PLAIN_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error generating reports: " + e.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSettings() {
        JOptionPane.showMessageDialog(this,
            "System Settings\n\n" +
            "Current Configuration:\n" +
            "• Database: SQLite (school_management.db)\n" +
            "• Students in DB: " + totalStudents + "\n" +
            "• Teachers in DB: " + totalTeachers + "\n" +
            "• System Version: Sprint 3.0\n" +
            "• Last Update: " + java.time.LocalDate.now() + "\n\n" +
            "Settings are loaded from the database.",
            "System Settings",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createCard(String title, String content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setForeground(new Color(80, 80, 80));
        contentArea.setEditable(false);
        contentArea.setBackground(Color.WHITE);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(120, 120, 120));
        
        // إضافة رمز صغير
        String icon = "";
        switch (title) {
            case "Total Students": icon = "👨‍🎓 "; break;
            case "Total Teachers": icon = "👩‍🏫 "; break;
            case "Total Salary": icon = "💰 "; break;
            case "System Status": icon = "🔧 "; break;
        }
        titleLabel.setText(icon + title);
        
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
    
    private void setupFrame() {
        setTitle("School Management System - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // إنشاء admin افتراضي للاختبار
            Admin testAdmin = new Admin("admin", "admin123", "System Administrator", "admin@school.com");
            MainFrame frame = new MainFrame(testAdmin);
            frame.setVisible(true);
        });
    }
}