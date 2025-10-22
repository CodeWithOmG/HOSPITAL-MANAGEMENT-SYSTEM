import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDateTime appointmentDate;
    private String diseaseType;
    private String status;
    
    // For display purposes
    private String patientName;
    private String doctorName;
    
    // Constructor for creating new appointment (without ID)
    public Appointment(int patientId, int doctorId, LocalDateTime appointmentDate, String diseaseType) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.diseaseType = diseaseType;
        this.status = "scheduled";
    }
    
    // Constructor for loading from database (with ID)
    public Appointment(int id, int patientId, int doctorId, LocalDateTime appointmentDate, 
                      String diseaseType, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.diseaseType = diseaseType;
        this.status = status;
    }
    
    // Full constructor with names for display
    public Appointment(int id, int patientId, int doctorId, LocalDateTime appointmentDate, 
                      String diseaseType, String status, String patientName, String doctorName) {
        this(id, patientId, doctorId, appointmentDate, diseaseType, status);
        this.patientName = patientName;
        this.doctorName = doctorName;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public String getDiseaseType() {
        return diseaseType;
    }
    
    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    @Override
    public String toString() {
        return String.format("Appointment: %s with Dr. %s on %s", 
                           patientName != null ? patientName : "Patient#" + patientId,
                           doctorName != null ? doctorName : "Doctor#" + doctorId,
                           appointmentDate);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Appointment appointment = (Appointment) obj;
        return id == appointment.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}