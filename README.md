
# 🏫 Basic School Data Management System

This project is part of the requirements for the course PGCS653 - Advanced Object-Oriented Programming, Omar Al-Mukhtar University.

---

## 🎯 Current Phase: Sprint 2 - Design & Implementation

This sprint focused on establishing the core structure of the application, implementing Advanced Object-Oriented Programming (OOP) concepts, and setting up the database connection.

### ⚙️ Technologies and Libraries Used

* Programming Language: Java 17
* Build Tool: Apache Maven
* Database: SQLite (using sqlite-jdbc dependency in `pom.xml`)
* Graphical User Interface (GUI): Swing / AWT

### 🏗️ Project Structure (Package Structure)

| Package | Description |
| :--- | :--- |
| wmesaf.basicschool.model | Contains core entities and applies OOP principles (Inheritance, Encapsulation, Polymorphism). |
| wmesaf.basicschool.interfaces | Contains Interfaces like IAuthenticatable for implementing polymorphism. |
| wmesaf.basicschool.database | Contains the DatabaseConnection class responsible for setting up the SQLite connection and creating tables. |
| wmesaf.basicschool.dao | Data Access Object (DAO) classes for handling basic database operations (CRUD) (e.g., AdminDAO, `StudentDAO`). |
| wmesaf.basicschool.gui | GUI classes such as LoginFrame and MainFrame. |
| wmesaf.basicschool | The main entry point for the application (`Main.java`). |

### 🔑 OOP Concepts Implemented in Sprint 2

The following concepts were implemented directly as required:

1.  Inheritance: Using the abstract class `Person` as the parent class for `Student` and `Teacher`.
2.  Encapsulation: All entity fields are private (`private`) and protected by public accessor methods (Getters/Setters).
3.  Polymorphism: Using the `IAuthenticatable` interface in the `Admin` class, and overriding the getRole() method in derived classes.
4.  Abstraction: Using the abstract class `Person` and the interface `IAuthenticatable`.
5.  Exception Handling: Extensive use of try-catch blocks in DAO classes and DatabaseConnection to handle database errors and invalid input.

---

## 💻 Running Instructions

1.  Cloning: Clone the repository using:
   
    git clone [YOUR_REPO_URL_HERE]
    
2.  Building (Maven): Navigate to the project folder and compile the files:
   
    mvn clean install
    
3.  Running: Run the application via Main.java or using the compiled JAR file:
   
    java -jar target/BasicSchool_Sprint2.jar
    
Login Credentials (Test Data):
* Username: admin
* Password: admin123