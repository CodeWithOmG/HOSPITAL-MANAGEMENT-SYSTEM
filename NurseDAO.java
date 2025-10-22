import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NurseDAO {
    
    public int addNurse(Nurse nurse) throws SQLException {
        String sql = "INSERT INTO nurses (name, license_id, department) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, nurse.getName());
            pstmt.setString(2, nurse.getLicenseId());
            pstmt.setString(3, nurse.getDepartment());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating nurse failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int nurseId = generatedKeys.getInt(1);
                    nurse.setId(nurseId);
                    return nurseId;
                } else {
                    throw new SQLException("Creating nurse failed, no ID obtained.");
                }
            }
        }
    }
    
    public Nurse getNurseById(int id) throws SQLException {
        String sql = "SELECT id, name, license_id, department FROM nurses WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Nurse(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("license_id"),
                        rs.getString("department")
                    );
                }
            }
        }
        return null;
    }
    
    public List<Nurse> getAllNurses() throws SQLException {
        List<Nurse> nurses = new ArrayList<>();
        String sql = "SELECT id, name, license_id, department FROM nurses ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                nurses.add(new Nurse(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("license_id"),
                    rs.getString("department")
                ));
            }
        }
        return nurses;
    }
    
    public boolean updateNurse(Nurse nurse) throws SQLException {
        String sql = "UPDATE nurses SET name = ?, license_id = ?, department = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nurse.getName());
            pstmt.setString(2, nurse.getLicenseId());
            pstmt.setString(3, nurse.getDepartment());
            pstmt.setInt(4, nurse.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteNurse(int id) throws SQLException {
        String sql = "DELETE FROM nurses WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Nurse> getNursesByDepartment(String department) throws SQLException {
        List<Nurse> nurses = new ArrayList<>();
        String sql = "SELECT id, name, license_id, department FROM nurses WHERE department = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    nurses.add(new Nurse(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("license_id"),
                        rs.getString("department")
                    ));
                }
            }
        }
        return nurses;
    }
    
    public boolean nurseExists(int id) throws SQLException {
        String sql = "SELECT 1 FROM nurses WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public boolean licenseIdExists(String licenseId) throws SQLException {
        String sql = "SELECT 1 FROM nurses WHERE license_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}