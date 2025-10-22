import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    
    public int addDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (name, qualification, specialization) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getQualification());
            pstmt.setString(3, doctor.getSpecialization());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating doctor failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int doctorId = generatedKeys.getInt(1);
                    doctor.setId(doctorId);
                    return doctorId;
                } else {
                    throw new SQLException("Creating doctor failed, no ID obtained.");
                }
            }
        }
    }
    
    public Doctor getDoctorById(int id) throws SQLException {
        String sql = "SELECT id, name, qualification, specialization FROM doctors WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("qualification"),
                        rs.getString("specialization")
                    );
                }
            }
        }
        return null;
    }
    
    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, name, qualification, specialization FROM doctors ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(new Doctor(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("qualification"),
                    rs.getString("specialization")
                ));
            }
        }
        return doctors;
    }
    
    public boolean updateDoctor(Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET name = ?, qualification = ?, specialization = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getQualification());
            pstmt.setString(3, doctor.getSpecialization());
            pstmt.setInt(4, doctor.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteDoctor(int id) throws SQLException {
        String sql = "DELETE FROM doctors WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Doctor> getDoctorsBySpecialization(String specialization) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, name, qualification, specialization FROM doctors WHERE specialization = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialization);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("qualification"),
                        rs.getString("specialization")
                    ));
                }
            }
        }
        return doctors;
    }
    
    public boolean doctorExists(int id) throws SQLException {
        String sql = "SELECT 1 FROM doctors WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}