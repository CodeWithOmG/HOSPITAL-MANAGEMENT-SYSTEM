-- Hospital Management System Database Schema
CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

-- Doctors table
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    qualification VARCHAR(200) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Nurses table
CREATE TABLE nurses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    license_id VARCHAR(50) UNIQUE NOT NULL,
    department VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patients table
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    contact VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Appointments table
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    appointment_date DATETIME NOT NULL,
    disease_type VARCHAR(100),
    status VARCHAR(20) DEFAULT 'scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- Insert some initial data
INSERT INTO doctors (name, qualification, specialization) VALUES
('Amit Gupta', 'MBBS, MD', 'Cardiology'),
('Sunita Sharma', 'MBBS, MS', 'Neurology');

INSERT INTO nurses (name, license_id, department) VALUES
('Priya Singh', 'N789123', 'ICU');