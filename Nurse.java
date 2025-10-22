public class Nurse {
    private int id;
    private String name;
    private String licenseId;
    private String department;
    
    // Constructor for creating new nurse (without ID)
    public Nurse(String name, String licenseId, String department) {
        this.name = name;
        this.licenseId = licenseId;
        this.department = department;
    }
    
    // Constructor for loading from database (with ID)
    public Nurse(int id, String name, String licenseId, String department) {
        this.id = id;
        this.name = name;
        this.licenseId = licenseId;
        this.department = department;
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
    
    public String getLicenseId() {
        return licenseId;
    }
    
    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return String.format("Nurse %s (ID: %s, Dept: %s)", name, licenseId, department);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Nurse nurse = (Nurse) obj;
        return id == nurse.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}