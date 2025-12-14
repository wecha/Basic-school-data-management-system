# 🏫 Advanced School Data Management System

[span_0](start_span)[span_1](start_span)This project is developed as part of the requirements for the PGCS653 - Advanced Object-Oriented Programming Course, Omar Al-Mukhtar University[span_0](end_span)[span_1](end_span).

---

## 🚀 Current Status: Advanced Architecture Implementation

This phase focuses on maintaining the existing system and restructuring it to apply a robust four-layer software architecture, integrating new features, and enhancing system reliability.

### ⚙️ Technologies and Libraries

| Category | Component | Detail |
| :--- | :--- | :--- |
| Language | Java | Version 17+ |
| Build Tool | Apache Maven | Used for dependency management and build lifecycle. |
| Database | SQLite | Lightweight, file-based database. |
| GUI | Swing / AWT | Standard Java libraries for the desktop interface. |

### 🏗️ Software Architecture: Four-Layer Model

The system is architecturally organized into four distinct layers to achieve better Separation of Concerns, maintainability, and scalability.

| Layer | Package | Core Responsibility | Implemented Classes (Examples) |
| :--- | :--- | :--- | :--- |
| Presentation | wmesaf.basicschool.gui | Handles user interaction and displays data (the Frames). | LoginFrame, MainFrame, StudentManagementFrame |
| Business Logic | wmesaf.basicschool.business | Contains all business rules, validation, and coordinates data flow between Presentation and DAO. | StudentService, ReportService, DashboardService |
| Data Access Object (DAO) | wmesaf.basicschool.dao | Provides CRUD operations and maps objects to the database structure. | StudentDAO, TeacherDAO, AdminDAO |
| Data/Database | wmesaf.basicschool.database | Manages the physical database connection and schema initialization. | DatabaseConnection, school_management.db |

### ✨ Implemented Core Features

The system currently supports the following key features, driven by the new architecture:

1.  [span_2](start_span)Version Control: Continual use of Git and GitHub to manage the code[span_2](end_span).
2.  [span_3](start_span)[span_4](start_span)Analytical Dashboard: A dedicated dashboard integrated into the MainFrame that shows at least three statistics in the system[span_3](end_span)[span_4](end_span).
3.  [span_5](start_span)[span_6](start_span)Reporting Module: Implementation of at least two reports accessible via the system[span_5](end_span)[span_6](end_span).
4.  CRUD Management: Complete Create, Read, Update, and Delete functionality for main entities.

### 🔑 Advanced OOP & Design Patterns

* OOP Principles: Applied across all entity (`model`) and service classes.
* [span_7](start_span)[span_8](start_span)Design Pattern: At least one design pattern has been applied to the code for efficiency and maintainability[span_7](end_span)[span_8](end_span). *(Replace this line with your specific pattern, e.g., **Singleton Pattern**)*.

---

## 💻 Running Instructions

1.  Prerequisites: Ensure Java 17+ and Maven are installed.
2.  Cloning: Clone the repository to your local machine:
       git clone [YOUR_REPO_URL_HERE]
    3.  Building: Navigate to the project root and build the application using Maven:
       mvn clean install
    4.  Running: Execute the compiled JAR file or run Main.java directly:
       java -jar target/BasicSchool_Final.jar
    
> Default Test Credentials:
> * Username: admin
> * Password: admin123