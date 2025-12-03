-- ============================================
-- School Management System - Database Schema
-- PGCS653 - Advanced OOP Course - Sprint 2
-- Created by: Wessal Mostafa Mohammed
-- Student ID: 262504
-- ============================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS school_management_system;
USE school_management_system;

-- ========== PERSONS TABLE ==========
-- Base table for all people (students and teachers)
CREATE TABLE IF NOT EXISTS persons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('STUDENT', 'TEACHER') NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(200),
    birth_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========== STUDENTS TABLE ==========
-- Student-specific information
CREATE TABLE IF NOT EXISTS students (
    person_id INT PRIMARY KEY,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    grade VARCHAR(10),
    enrollment_date DATE,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id)
);

-- ========== TEACHERS TABLE ==========
-- Teacher-specific information
CREATE TABLE IF NOT EXISTS teachers (
    person_id INT PRIMARY KEY,
    teacher_id VARCHAR(20) UNIQUE NOT NULL,
    subject VARCHAR(50),
    salary DECIMAL(10, 2),
    hire_date DATE,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    INDEX idx_teacher_id (teacher_id)
);

-- ========== ADMINS TABLE ==========
-- System administrators
CREATE TABLE IF NOT EXISTS admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
);

-- ========== ADDITIONAL TABLES (Optional) ==========
-- Courses table for future expansion
CREATE TABLE IF NOT EXISTS courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    teacher_id INT,
    credits INT DEFAULT 3,
    FOREIGN KEY (teacher_id) REFERENCES teachers(person_id)
);

-- ========== COMMENTS ==========
-- Table comments for documentation
ALTER TABLE persons COMMENT = 'Base table for all persons in the system';
ALTER TABLE students COMMENT = 'Students information extending persons';
ALTER TABLE teachers COMMENT = 'Teachers information extending persons';
ALTER TABLE admins COMMENT = 'System administrators for login and management';

-- ========== END OF SCHEMA ==========