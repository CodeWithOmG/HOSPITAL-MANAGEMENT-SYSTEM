public class Patient {
    private int id;
    private String name;
    private int age;
    private String contact;
    
    // Constructor for creating new patient (without ID)
    public Patient(String name, int age, String contact) {
        this.name = name;
        this.age = age;
        this.contact = contact;
    }
    
    // Constructor for loading from database (with ID)
    public Patient(int id, String name, int age, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
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
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    @Override
    public String toString() {
        return String.format("%s (Age: %d, Contact: %s)", name, age, contact);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Patient patient = (Patient) obj;
        return id == patient.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}