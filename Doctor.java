public class Doctor {
    private int id;
    private String name;
    private String qualification;
    private String specialization;
    
    // Constructor for creating new doctor (without ID)
    public Doctor(String name, String qualification, String specialization) {
        this.name = name;
        this.qualification = qualification;
        this.specialization = specialization;
    }
    
    // Constructor for loading from database (with ID)
    public Doctor(int id, String name, String qualification, String specialization) {
        this.id = id;
        this.name = name;
        this.qualification = qualification;
        this.specialization = specialization;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    @Override
    public String toString() {
        return String.format("Dr. %s (%s, %s)", name, qualification, specialization);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Doctor doctor = (Doctor) obj;
        return id == doctor.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}