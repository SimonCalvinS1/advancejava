package DoctorAppointmentManager.javabeans_project;

//javac -cp "..;..\mysql-connector-j-9.2.0.jar" MeetingScheduler.java
//java -cp "..;..\mysql-connector-j-9.2.0.jar" MeetingScheduler.java

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class MeetingScheduler extends JFrame {
    private MeetingServiceBean service = new MeetingServiceBean();
    private JTextField patientField;
    private JComboBox<String> typeCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public MeetingScheduler() {
        setTitle("Medi-Connect Pro - Enterprise Scheduler");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- TOP: Registration Form ---
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Quick Schedule"));
        
        patientField = new JTextField(15);
        typeCombo = new JComboBox<>(new String[]{"Consultation", "Emergency", "Follow-up"});
        JButton saveBtn = new JButton("Book Appointment");
        saveBtn.setBackground(new Color(41, 128, 185));
        saveBtn.setForeground(Color.WHITE);

        formPanel.add(new Label("Patient:")); formPanel.add(patientField);
        formPanel.add(new Label("Type:")); formPanel.add(typeCombo);
        formPanel.add(saveBtn);

        // --- CENTER: Data Table ---
        String[] columns = {"ID", "Patient Name", "Meeting Type", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- RIGHT: Action Sidebar ---
        JPanel sideBar = new JPanel(new GridLayout(6, 1, 5, 5));
        JButton btnComplete = new JButton("Mark Completed");
        JButton btnCancel = new JButton("Cancel Appt");
        JButton btnRefresh = new JButton("Refresh List");
        
        sideBar.add(btnComplete); sideBar.add(btnCancel); 
        sideBar.add(new JSeparator()); sideBar.add(btnRefresh);

        // --- BOTTOM: Status Bar ---
        statusLabel = new JLabel(" System Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());

        // Layout Assembly
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(sideBar, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);

        // Listeners
        saveBtn.addActionListener(e -> handleSave());
        btnRefresh.addActionListener(e -> loadTableData());
        btnComplete.addActionListener(e -> updateSelectedStatus("Completed"));
        btnCancel.addActionListener(e -> updateSelectedStatus("Cancelled"));

        loadTableData();
    }

    private void handleSave() {
        if (patientField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a patient name.");
            return;
        }
        MeetingBean bean = new MeetingBean();
        bean.setPatientName(patientField.getText());
        bean.setMeetingType((String) typeCombo.getSelectedItem());
        bean.setTimestamp(LocalDateTime.now());
        bean.setRemarks("System Generated Entry");

        if (service.scheduleMeeting(bean)) {
            statusLabel.setText(" Appointment saved for " + bean.getPatientName());
            loadTableData();
            patientField.setText("");
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a meeting from the table first.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (service.updateStatus(id, newStatus)) {
            statusLabel.setText(" Meeting ID " + id + " marked as " + newStatus);
            loadTableData();
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<MeetingBean> meetings = service.getAllMeetings();
        for (MeetingBean m : meetings) {
            tableModel.addRow(new Object[]{m.getMeetingId(), m.getPatientName(), m.getMeetingType(), m.getStatus()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MeetingScheduler().setVisible(true));
    }

    class MeetingBean implements Serializable {
        private int meetingId;
        private String patientName;
        private String meetingType;
        private String status;
        private LocalDateTime timestamp;
        private String remarks;

        // No-argument constructor
        public MeetingBean() {}

        // Getters and Setters
        public int getMeetingId() { return meetingId; }

        public void setMeetingId(int meetingId) { this.meetingId = meetingId; }

        public String getPatientName() { return patientName; }

        public void setPatientName(String patientName) { this.patientName = patientName; }

        public String getMeetingType() { return meetingType; }

        public void setMeetingType(String meetingType) { this.meetingType = meetingType; }

        public String getStatus() { return status; }

        public void setStatus(String status) { this.status = status; }

        public LocalDateTime getTimestamp() { return timestamp; }

        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public String getRemarks() { return remarks; }

        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    class MeetingServiceBean {
        private final String URL = "jdbc:mysql://localhost:3306/doctor_db";
        private final String USER = "newuser";
        private final String PASS = "password123";

        public MeetingServiceBean() {
            try { Class.forName("com.mysql.cj.jdbc.Driver"); } 
            catch (Exception e) { e.printStackTrace(); }
        }

        public List<MeetingBean> getAllMeetings() {
            List<MeetingBean> list = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM doctor_meetings ORDER BY meeting_timestamp DESC")) {
                while (rs.next()) {
                    MeetingBean b = new MeetingBean();
                    b.setMeetingId(rs.getInt("meeting_id"));
                    b.setPatientName(rs.getString("patient_name"));
                    b.setMeetingType(rs.getString("meeting_type"));
                    b.setStatus(rs.getString("status"));
                    list.add(b);
                }
            } catch (SQLException e) { e.printStackTrace(); }
            return list;
        }

        public boolean updateStatus(int id, String newStatus) {
            String sql = "UPDATE doctor_meetings SET status = ? WHERE meeting_id = ?";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { return false; }
        }

        public boolean scheduleMeeting(MeetingBean meeting) {
            String sql = "INSERT INTO doctor_meetings (patient_name, meeting_type, status, meeting_timestamp, remarks) VALUES (?, ?, ?, ?, ?)";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, meeting.getPatientName());
                ps.setString(2, meeting.getMeetingType());
                ps.setString(3, meeting.getStatus());
                ps.setString(4, meeting.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                ps.setString(5, meeting.getRemarks());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        }
    }
}
