import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class HospitalManagementSystemGUI extends JFrame {

    // Database Access Objects
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final NurseDAO nurseDAO = new NurseDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    // Cache for current data
    private List<Doctor> doctorsList = new ArrayList<>();
    private List<Nurse> nursesList = new ArrayList<>();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private JPanel loadingPanel, managementPanel, appointmentPanel,
            addDoctorPanel, addNursePanel, confirmationPanel, letterPanel;

    private LoginModernPanel loginPanel;
    private JPanel hospitalPanel = new JPanel(new BorderLayout(10, 10));

    private JTextField patientNameField, patientAgeField, patientContactField, appointmentDateField;
    private JComboBox<String> diseaseComboBox, doctorComboBox;

    private JTextField doctorNameField, doctorQualField;
    private JComboBox<String> doctorSpecComboBox;

    private JTextField nurseNameField, nurseIdField;
    private JComboBox<String> nurseDeptComboBox;

    private JTable doctorTable, nurseTable;
    private DefaultTableModel doctorTableModel, nurseTableModel;

    private JProgressBar progressBar;
    private JTextArea appointmentLetterText;
    private JLabel confirmationMessageLabel;

    private String currentPatientName, currentPatientAge, currentPatientContact, currentAppointmentDate, currentDisease, currentDoctor;
    private Patient currentPatient;
    private Doctor currentDoctorObj;

    public HospitalManagementSystemGUI() {
        setTitle("SRIOM Hospital Management System");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
        setupPanels();
        initActionListeners();
        loadInitialData();

        mainPanel.add(loadingPanel, "loading");
        mainPanel.add(loginPanel, "login");
        mainPanel.add(hospitalPanel, "hospital");
        mainPanel.add(managementPanel, "management");
        mainPanel.add(appointmentPanel, "appointment");
        mainPanel.add(addDoctorPanel, "addDoctor");
        mainPanel.add(addNursePanel, "addNurse");
        mainPanel.add(confirmationPanel, "confirmation");
        mainPanel.add(letterPanel, "letter");

        add(mainPanel);

        // Add shutdown hook to close database connections
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeDataSource();
        }));

        cardLayout.show(mainPanel, "loading");
    }

    private void initComponents() {
        progressBar = new JProgressBar(0, 100);
        appointmentLetterText = new JTextArea();
        appointmentLetterText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        appointmentLetterText.setEditable(false);
        confirmationMessageLabel = new JLabel("", SwingConstants.CENTER);
        confirmationMessageLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        patientNameField = new JTextField(20);
        patientAgeField = new JTextField(5);
        patientContactField = new JTextField(15);
        appointmentDateField = new JTextField(15);

        diseaseComboBox = new JComboBox<>(new String[]{"Fever","LegPain", "Diabetes", "Hypertension", "Other"});
        doctorComboBox = new JComboBox<>();

        doctorNameField = new JTextField(20);
        doctorQualField = new JTextField(20);
        doctorSpecComboBox = new JComboBox<>(new String[]{"General Medicine", "Cardiology", "Dermatology", "Orthopedics", "Neurology"});

        nurseNameField = new JTextField(20);
        nurseIdField = new JTextField(15);
        nurseDeptComboBox = new JComboBox<>(new String[]{"Emergency", "ICU", "Surgical Ward", "Maternity Ward"});

        doctorTableModel = new DefaultTableModel(new String[]{"Name", "Qualification", "Specialization"}, 0);
        nurseTableModel = new DefaultTableModel(new String[]{"Name", "License ID", "Department"}, 0);

        doctorTable = new JTable(doctorTableModel);
        nurseTable = new JTable(nurseTableModel);
        doctorTable.setFillsViewportHeight(true);
        nurseTable.setFillsViewportHeight(true);
    }

    private void setupPanels() {
        setupLoadingPanel();
        loginPanel = new LoginModernPanel();
        setupHospitalPanel();
        setupManagementPanel();
        setupAppointmentPanel();
        setupAddDoctorPanel();
        setupAddNursePanel();
        setupConfirmationPanel();
        setupLetterPanel();
    }

    private void setupLoadingPanel() {
        loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(new Color(230, 240, 255)); // Light blue
        JLabel loadingTitle = new JLabel("SRIOM HOSPITAL", SwingConstants.CENTER);
        loadingTitle.setFont(new Font("SansSerif", Font.BOLD, 42));
        loadingTitle.setForeground(new Color(10, 60, 130));
        loadingPanel.add(loadingTitle, BorderLayout.CENTER);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 30));
        JPanel pbPanel = new JPanel();
        pbPanel.setBackground(new Color(230, 240, 255));
        pbPanel.add(progressBar);
        loadingPanel.add(pbPanel, BorderLayout.SOUTH);
    }

    private void setupHospitalPanel() {
        hospitalPanel.setBackground(new Color(245, 245, 245)); // Very light gray
        hospitalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Hospital Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(10, 60, 130));
        hospitalPanel.add(title, BorderLayout.NORTH);

        JTabbedPane staffTabs = new JTabbedPane();
        staffTabs.addTab("Doctors", new JScrollPane(doctorTable));
        staffTabs.addTab("Nurses", new JScrollPane(nurseTable));
        hospitalPanel.add(staffTabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton mgmtButton = createStyledButton("Patient & Staff Management");
        mgmtButton.addActionListener(e -> cardLayout.show(mainPanel, "management"));
        buttonPanel.add(mgmtButton);

        JButton logoutButton = createStyledButton("Logout");
        logoutButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        buttonPanel.add(logoutButton);

        hospitalPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupManagementPanel() {
        managementPanel = createBackgroundPanel();

        JPanel transparentPanel = new JPanel(null);
        transparentPanel.setBackground(new Color(200, 220, 255, 220)); // translucent light blue
        transparentPanel.setBounds(100, 100, 600, 350);

        managementPanel.add(transparentPanel);

        JButton addPatientBtn = createMenuButton("ADD PATIENT", "patient_icon.png");
        addPatientBtn.setBounds(50, 100, 150, 150);
        addPatientBtn.addActionListener(e -> cardLayout.show(mainPanel, "appointment"));
        transparentPanel.add(addPatientBtn);

        JButton addDoctorBtn = createMenuButton("ADD DOCTOR", "doctor_icon.png");
        addDoctorBtn.setBounds(225, 100, 150, 150);
        addDoctorBtn.addActionListener(e -> cardLayout.show(mainPanel, "addDoctor"));
        transparentPanel.add(addDoctorBtn);

        JButton addNurseBtn = createMenuButton("ADD NURSE", "nurse_icon.png");
        addNurseBtn.setBounds(400, 100, 150, 150);
        addNurseBtn.addActionListener(e -> cardLayout.show(mainPanel, "addNurse"));
        transparentPanel.add(addNurseBtn);

        JButton backBtn = createStyledButton("Back to Dashboard");
        backBtn.setBounds(450, 300, 160, 35);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "hospital"));
        transparentPanel.add(backBtn);
    }

    private void setupAppointmentPanel() {
        appointmentPanel = new JPanel(new GridBagLayout());
        appointmentPanel.setBackground(new Color(240, 255, 240)); // Light green

        GridBagConstraints gbc = createGbcForForm();

        JLabel title = new JLabel("Patient Appointment Form", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(10, 90, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        appointmentPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        appointmentPanel.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx = 1;
        appointmentPanel.add(patientNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        appointmentPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        appointmentPanel.add(patientAgeField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        appointmentPanel.add(new JLabel("Contact No:"), gbc);
        gbc.gridx = 1;
        appointmentPanel.add(patientContactField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        appointmentPanel.add(new JLabel("Appointment Date:"), gbc);
        gbc.gridx = 1;
        appointmentPanel.add(appointmentDateField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        appointmentPanel.add(new JLabel("Disease Type:"), gbc);
        gbc.gridx = 1;
        appointmentPanel.add(diseaseComboBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        appointmentPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1;
        appointmentPanel.add(doctorComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 255, 240));
        JButton submitBtn = createColoredButton("Submit", new Color(20, 135, 20));
        submitBtn.addActionListener(e -> submitAppointment());
        JButton backBtn = createColoredButton("Back", new Color(160, 160, 160));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "management"));
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        appointmentPanel.add(buttonPanel, gbc);
    }

    private void setupAddDoctorPanel() {
        addDoctorPanel = new JPanel(new GridBagLayout());
        addDoctorPanel.setBackground(new Color(255, 240, 240)); // Light pink/red

        GridBagConstraints gbc = createGbcForForm();

        JLabel title = new JLabel("Add New Doctor", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(135, 20, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addDoctorPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        addDoctorPanel.add(new JLabel("Doctor Name:"), gbc);
        gbc.gridx = 1;
        addDoctorPanel.add(doctorNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        addDoctorPanel.add(new JLabel("Qualification:"), gbc);
        gbc.gridx = 1;
        addDoctorPanel.add(doctorQualField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        addDoctorPanel.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 1;
        addDoctorPanel.add(doctorSpecComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 240, 240));
        JButton submitBtn = createColoredButton("Submit", new Color(190, 20, 20));
        submitBtn.addActionListener(e -> addDoctor());
        JButton backBtn = createColoredButton("Back", new Color(160, 160, 160));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "management"));
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        addDoctorPanel.add(buttonPanel, gbc);
    }

    private void setupAddNursePanel() {
        addNursePanel = new JPanel(new GridBagLayout());
        addNursePanel.setBackground(new Color(240, 240, 255)); // Light purple/blue

        GridBagConstraints gbc = createGbcForForm();

        JLabel title = new JLabel("Add New Nurse", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(40, 40, 130));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addNursePanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        addNursePanel.add(new JLabel("Nurse Name:"), gbc);
        gbc.gridx = 1;
        addNursePanel.add(nurseNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        addNursePanel.add(new JLabel("License ID:"), gbc);
        gbc.gridx = 1;
        addNursePanel.add(nurseIdField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        addNursePanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        addNursePanel.add(nurseDeptComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 240, 255));
        JButton submitBtn = createColoredButton("Submit", new Color(20, 20, 190));
        submitBtn.addActionListener(e -> addNurse());
        JButton backBtn = createColoredButton("Back", new Color(160, 160, 160));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "management"));
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        addNursePanel.add(buttonPanel, gbc);
    }

    private void setupConfirmationPanel() {
        confirmationPanel = new JPanel(new BorderLayout());
        confirmationPanel.setBackground(new Color(255, 255, 230));

        JLabel title = new JLabel("Appointment Confirmation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(120, 90, 10));
        confirmationPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(255, 255, 230));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        confirmationMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(confirmationMessageLabel, BorderLayout.CENTER);

        JButton viewLetterBtn = createColoredButton("View Appointment Letter", new Color(150, 120, 20));
        viewLetterBtn.addActionListener(e -> {
            generateAppointmentLetter();
            cardLayout.show(mainPanel, "letter");
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(255, 255, 230));
        btnPanel.add(viewLetterBtn);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        confirmationPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void setupLetterPanel() {
        letterPanel = new JPanel(new BorderLayout());
        letterPanel.setBackground(new Color(245, 255, 255)); // Pale cyan

        JLabel title = new JLabel("Appointment Letter", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(10, 80, 80));
        letterPanel.add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(appointmentLetterText);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Letter Content"),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        letterPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = createColoredButton("Back", new Color(160, 160, 160));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "confirmation"));
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 255, 255));
        btnPanel.add(backBtn);
        letterPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    private GridBagConstraints createGbcForForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,15,10,15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private JButton createColoredButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private JButton createStyledButton(String text) {
        Color bgColor = new Color(50, 90, 170);
        return createColoredButton(text, bgColor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalManagementSystemGUI().setVisible(true));
    }

    private void initActionListeners() {
        loginPanel.loginButton.addActionListener(e -> {
            String user = loginPanel.emailField.getText();
            String pass = new String(loginPanel.passwordField.getPassword());
            if (user.equals("admin") && pass.equals("admin123")) {
                cardLayout.show(mainPanel, "hospital");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        Timer startTimer = new Timer(50, e -> {
            int val = progressBar.getValue();
            if (val < 100) progressBar.setValue(val + 2);
            else {
                ((Timer) e.getSource()).stop();
                cardLayout.show(mainPanel, "login");
            }
        });
        startTimer.start();
    }

    private void submitAppointment() {
        if (patientNameField.getText().isEmpty() || patientAgeField.getText().isEmpty() ||
                patientContactField.getText().isEmpty() || appointmentDateField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all patient appointment fields.");
            return;
        }
        
        try {
            // Parse patient data
            String name = patientNameField.getText().trim();
            int age;
            try {
                age = Integer.parseInt(patientAgeField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age.");
                return;
            }
            String contact = patientContactField.getText().trim();
            
            // Check if patient already exists
            currentPatient = patientDAO.findPatientByContact(contact);
            if (currentPatient == null) {
                // Create new patient
                currentPatient = new Patient(name, age, contact);
                patientDAO.addPatient(currentPatient);
            }
            
            // Parse appointment date
            LocalDateTime appointmentDateTime;
            try {
                appointmentDateTime = LocalDateTime.parse(appointmentDateField.getText().trim() + ":00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Please enter date in format: yyyy-MM-dd HH:mm");
                return;
            }
            
            // Get selected doctor
            int selectedDoctorIndex = doctorComboBox.getSelectedIndex();
            if (selectedDoctorIndex < 0 || selectedDoctorIndex >= doctorsList.size()) {
                JOptionPane.showMessageDialog(this, "Please select a valid doctor.");
                return;
            }
            currentDoctorObj = doctorsList.get(selectedDoctorIndex);
            
            // Create appointment
            String diseaseType = (String) diseaseComboBox.getSelectedItem();
            Appointment appointment = new Appointment(currentPatient.getId(), currentDoctorObj.getId(), 
                appointmentDateTime, diseaseType);
            appointmentDAO.addAppointment(appointment);
            
            // Update current variables for display
            currentPatientName = name;
            currentPatientAge = String.valueOf(age);
            currentPatientContact = contact;
            currentAppointmentDate = appointmentDateField.getText().trim();
            currentDisease = diseaseType;
            currentDoctor = currentDoctorObj.toString();
            
            confirmationMessageLabel.setText("<html>Appointment for <b>" + currentPatientName + "</b> is booked.<br/>Doctor: " + currentDoctor + "<br/>Date: " + currentAppointmentDate + "</html>");
            cardLayout.show(mainPanel, "confirmation");

            clearAppointmentFields();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating appointment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearAppointmentFields() {
        patientNameField.setText("");
        patientAgeField.setText("");
        patientContactField.setText("");
        appointmentDateField.setText("");
        diseaseComboBox.setSelectedIndex(0);
        doctorComboBox.setSelectedIndex(0);
    }

    private void addDoctor() {
        String name = doctorNameField.getText().trim();
        String qual = doctorQualField.getText().trim();
        if (name.isEmpty() || qual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all doctor fields.");
            return;
        }
        String spec = (String) doctorSpecComboBox.getSelectedItem();
        Doctor newDoctor = new Doctor(name, qual, spec);
        
        try {
            doctorDAO.addDoctor(newDoctor);
            JOptionPane.showMessageDialog(this, "Doctor Added Successfully!");
            refreshDoctorTable();
            refreshDoctorComboBox();

            doctorNameField.setText("");
            doctorQualField.setText("");
            doctorSpecComboBox.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding doctor: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addNurse() {
        String name = nurseNameField.getText().trim();
        String id = nurseIdField.getText().trim();
        if (name.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all nurse fields.");
            return;
        }
        String dept = (String) nurseDeptComboBox.getSelectedItem();
        Nurse newNurse = new Nurse(name, id, dept);
        
        try {
            // Check if license ID already exists
            if (nurseDAO.licenseIdExists(id)) {
                JOptionPane.showMessageDialog(this, "License ID already exists. Please use a different ID.", "Duplicate License ID", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            nurseDAO.addNurse(newNurse);
            JOptionPane.showMessageDialog(this, "Nurse Added Successfully!");
            refreshNurseTable();

            nurseNameField.setText("");
            nurseIdField.setText("");
            nurseDeptComboBox.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding nurse: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshDoctorTable() {
        try {
            doctorsList = doctorDAO.getAllDoctors();
            doctorTableModel.setRowCount(0);
            for (Doctor doc : doctorsList) {
                doctorTableModel.addRow(new Object[]{doc.getName(), doc.getQualification(), doc.getSpecialization()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshNurseTable() {
        try {
            nursesList = nurseDAO.getAllNurses();
            nurseTableModel.setRowCount(0);
            for (Nurse nurse : nursesList) {
                nurseTableModel.addRow(new Object[]{nurse.getName(), nurse.getLicenseId(), nurse.getDepartment()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading nurses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshDoctorComboBox() {
        try {
            doctorsList = doctorDAO.getAllDoctors();
            doctorComboBox.removeAllItems();
            for (Doctor d : doctorsList) {
                doctorComboBox.addItem(d.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors for combo box: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadInitialData() {
        // Load data from database
        refreshDoctorTable();
        refreshNurseTable();
        refreshDoctorComboBox();
        
        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this, "Warning: Could not connect to database. Please check your database configuration.", 
                "Database Connection Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void generateAppointmentLetter() {
        String formattedDate = "";
        try {
            LocalDateTime dt = LocalDateTime.parse(currentAppointmentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            formattedDate = dt.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"));
        } catch (Exception e) {
            formattedDate = currentAppointmentDate;
        }

        String letterContent = "APPOINTMENT LETTER\n\n"
                + "Patient Name: " + currentPatientName + "\n"
                + "Age: " + currentPatientAge + "\n"
                + "Contact: " + currentPatientContact + "\n"
                + "Appointment Date: " + formattedDate + "\n"
                + "Disease Type: " + currentDisease + "\n"
                + "Consulting Doctor: " + currentDoctor + "\n\n"
                + "Please arrive 15 minutes before your appointment time.\n"
                + "Thank you for choosing SRIOM Hospital.\n";

        appointmentLetterText.setText(letterContent);
    }

    private JPanel createBackgroundPanel() {
        return new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = new ImageIcon("login_background.jpg").getImage();
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(Color.LIGHT_GRAY);
                }
            }
        };
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            button.setIcon(icon);
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setContentAreaFilled(false);
        return button;
    }

    static class LoginModernPanel extends JPanel {
        public JTextField emailField = new JTextField("admin");
        public JPasswordField passwordField = new JPasswordField("admin123");
        public JButton loginButton = new JButton("Login");
        private Image backgroundImage;

        public LoginModernPanel() {
            setLayout(null);
            try {
                backgroundImage = new ImageIcon("login_background.jpg").getImage();
            } catch (Exception e) {
                setBackground(new Color(42, 46, 52));
            }
            JLabel systemTitle = new JLabel("HOSPITAL MANAGEMENT SYSTEM", SwingConstants.CENTER);
            systemTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
            systemTitle.setForeground(Color.DARK_GRAY);
            systemTitle.setBounds(0, 100, 800, 40);
            add(systemTitle);
            emailField.setBounds(300, 180, 200, 40);
            add(emailField);
            passwordField.setBounds(300, 240, 200, 40);
            add(passwordField);
            loginButton.setBackground(new Color(75, 175, 220));
            loginButton.setForeground(Color.WHITE);
            loginButton.setBounds(320, 300, 160, 40);
            add(loginButton);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}