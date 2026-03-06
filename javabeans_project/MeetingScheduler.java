package DoctorAppointmentManager.javabeans_project;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MeetingScheduler extends JFrame {
    private MeetingServiceBean service = new MeetingServiceBean();
    private JTextField patientField;
    private JComboBox<String> typeCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);  // Green
    private final Color DANGER_COLOR = new Color(192, 57, 43);   // Red
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    private final Color SIDEBAR_COLOR = new Color(44, 62, 80);   // Dark Navy

    public MeetingScheduler() {
        setTitle("Medi-Connect Pro - Enterprise Scheduler");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new MatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        
        JLabel titleLabel = new JLabel("Quick Booking:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        patientField = new JTextField(15);
        patientField.setPreferredSize(new Dimension(200, 30));
        
        typeCombo = new JComboBox<>(new String[]{"Consultation", "Emergency", "Follow-up"});
        typeCombo.setPreferredSize(new Dimension(150, 30));
        
        JButton saveBtn = createStyledButton("Book Appointment", SUCCESS_COLOR);

        formPanel.add(titleLabel);
        formPanel.add(new JLabel("Patient:")); formPanel.add(patientField);
        formPanel.add(new JLabel("Type:")); formPanel.add(typeCombo);
        formPanel.add(saveBtn);

        String[] columns = {"ID", "Patient Name", "Meeting Type", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JPanel sideBar = new JPanel(new GridLayout(8, 1, 10, 15));
        sideBar.setBackground(SIDEBAR_COLOR);
        sideBar.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        JLabel sideTitle = new JLabel("ACTIONS");
        sideTitle.setForeground(Color.WHITE);
        sideTitle.setHorizontalAlignment(SwingConstants.CENTER);
        sideTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnComplete = createStyledButton("Mark Completed", SUCCESS_COLOR);
        JButton btnCancel = createStyledButton("Cancel Appt", DANGER_COLOR);
        JButton btnRefresh = createStyledButton("Refresh List", PRIMARY_COLOR);
        
        sideBar.add(sideTitle);
        sideBar.add(btnComplete); 
        sideBar.add(btnCancel); 
        sideBar.add(new JSeparator()); 
        sideBar.add(btnRefresh);

        statusLabel = new JLabel(" System Ready");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(PRIMARY_COLOR);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setPreferredSize(new Dimension(100, 30));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(sideBar, BorderLayout.WEST); // Moved to West for a modern dashboard look
        add(statusLabel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> handleSave());
        btnRefresh.addActionListener(e -> loadTableData());
        btnComplete.addActionListener(e -> updateSelectedStatus("Completed"));
        btnCancel.addActionListener(e -> updateSelectedStatus("Cancelled"));

        loadTableData();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setGridColor(BACKGROUND_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SIDEBAR_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
    }

    private void handleSave() {
        if (patientField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a patient name.");
            return;
        }
        MeetingBean bean = new MeetingBean();
        bean.setPatientName(patientField.getText());
        bean.setMeetingType((String) typeCombo.getSelectedItem());
        bean.setStatus("Scheduled");
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

    // --- (Include your inner classes MeetingBean and MeetingServiceBean here) ---
    class MeetingBean implements Serializable {
        private int meetingId;
        private String patientName;
        private String meetingType;
        private String status;
        private LocalDateTime timestamp;
        private String remarks;
        public MeetingBean() {}
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
