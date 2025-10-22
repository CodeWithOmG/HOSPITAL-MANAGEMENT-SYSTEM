import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    public int addAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, disease_type, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDate()));
            pstmt.setString(4, appointment.getDiseaseType());
            pstmt.setString(5, appointment.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating appointment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int appointmentId = generatedKeys.getInt(1);
                    appointment.setId(appointmentId);
                    return appointmentId;
                } else {
                    throw new SQLException("Creating appointment failed, no ID obtained.");
                }
            }
        }
    }
    
    public Appointment getAppointmentById(int id) throws SQLException {
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, a.appointment_date, " +
                     "a.disease_type, a.status, p.name as patient_name, d.name as doctor_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getTimestamp("appointment_date").toLocalDateTime(),
                        rs.getString("disease_type"),
                        rs.getString("status"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name")
                    );
                }
            }
        }
        return null;
    }
    
    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, a.appointment_date, " +
                     "a.disease_type, a.status, p.name as patient_name, d.name as doctor_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                     "ORDER BY a.appointment_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("id"),
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getTimestamp("appointment_date").toLocalDateTime(),
                    rs.getString("disease_type"),
                    rs.getString("status"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name")
                ));
            }
        }
        return appointments;
    }
    
    public boolean updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, appointment_date = ?, " +
                     "disease_type = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDate()));
            pstmt.setString(4, appointment.getDiseaseType());
            pstmt.setString(5, appointment.getStatus());
            pstmt.setInt(6, appointment.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAppointment(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Appointment> getAppointmentsByPatient(int patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, a.appointment_date, " +
                     "a.disease_type, a.status, p.name as patient_name, d.name as doctor_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.patient_id = ? ORDER BY a.appointment_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getTimestamp("appointment_date").toLocalDateTime(),
                        rs.getString("disease_type"),
                        rs.getString("status"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name")
                    ));
                }
            }
        }
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByDoctor(int doctorId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, a.appointment_date, " +
                     "a.disease_type, a.status, p.name as patient_name, d.name as doctor_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.doctor_id = ? ORDER BY a.appointment_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getTimestamp("appointment_date").toLocalDateTime(),
                        rs.getString("disease_type"),
                        rs.getString("status"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name")
                    ));
                }
            }
        }
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByStatus(String status) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, a.appointment_date, " +
                     "a.disease_type, a.status, p.name as patient_name, d.name as doctor_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.status = ? ORDER BY a.appointment_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getTimestamp("appointment_date").toLocalDateTime(),
                        rs.getString("disease_type"),
                        rs.getString("status"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name")
                    ));
                }
            }
        }
        return appointments;
    }
    
    public boolean appointmentExists(int id) throws SQLException {
        String sql = "SELECT 1 FROM appointments WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, a.appointment_date, " +
                     "a.disease_type, a.status, p.name as patient_name, d.name as doctor_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.appointment_date BETWEEN ? AND ? ORDER BY a.appointment_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getTimestamp("appointment_date").toLocalDateTime(),
                        rs.getString("disease_type"),
                        rs.getString("status"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name")
                    ));
                }
            }
        }
        return appointments;
    }
}