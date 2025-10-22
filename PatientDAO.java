import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    
    public int addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, age, contact) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getContact());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating patient failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int patientId = generatedKeys.getInt(1);
                    patient.setId(patientId);
                    return patientId;
                } else {
                    throw new SQLException("Creating patient failed, no ID obtained.");
                }
            }
        }
    }
    
    public Patient getPatientById(int id) throws SQLException {
        String sql = "SELECT id, name, age, contact FROM patients WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("contact")
                    );
                }
            }
        }
        return null;
    }
    
    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, name, age, contact FROM patients ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(new Patient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("contact")
                ));
            }
        }
        return patients;
    }
    
    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name = ?, age = ?, contact = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getContact());
            pstmt.setInt(4, patient.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deletePatient(int id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Patient> searchPatients(String searchTerm) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, name, age, contact FROM patients WHERE " +
                     "name LIKE ? OR contact LIKE ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("contact")
                    ));
                }
            }
        }
        return patients;
    }
    
    public boolean patientExists(int id) throws SQLException {
        String sql = "SELECT 1 FROM patients WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public Patient findPatientByContact(String contact) throws SQLException {
        String sql = "SELECT id, name, age, contact FROM patients WHERE contact = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, contact);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("contact")
                    );
                }
            }
        }
        return null;
    }
}