package DoctorAppointmentManager.rmiproject;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DoctorMeetingSwingClient extends UnicastRemoteObject
        implements ClientCallback {

    private RemoteService service;

    private JFrame frame;
    private JTextField doctorField;
    private JTextField patientField;
    private JComboBox<String> priorityBox;
    private JTextArea outputArea;
    private JLabel statusLabel;

    protected DoctorMeetingSwingClient() throws Exception {
        super();
        initializeGUI();
        connectToServer();
    }

    private void initializeGUI() {

        frame = new JFrame("Hospital Remote Booking Portal");
        frame.setSize(650, 550);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("New Appointment"));

        doctorField = new JTextField();
        patientField = new JTextField();
        priorityBox = new JComboBox<>(new String[]{
                "Emergency", "Routine", "Follow-up"
        });

        topPanel.add(new JLabel("Doctor Name:"));
        topPanel.add(doctorField);

        topPanel.add(new JLabel("Patient Name:"));
        topPanel.add(patientField);

        topPanel.add(new JLabel("Priority:"));
        topPanel.add(priorityBox);

        JButton bookBtn = new JButton("Book Meeting");
        JButton emergencyBtn = new JButton("View Emergencies");

        topPanel.add(bookBtn);
        topPanel.add(emergencyBtn);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        statusLabel = new JLabel("Server: Connecting...");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        bookBtn.addActionListener(e -> bookMeeting());
        emergencyBtn.addActionListener(e -> loadEmergencies());

        frame.setVisible(true);
    }

    private void connectToServer() {
        try {
            service = (RemoteService)
                    Naming.lookup("rmi://localhost/DoctorService");

            service.registerClient(this);

            statusLabel.setText(service.getServerStatus());
            outputArea.append("Connected successfully.\n\n");

        } catch (Exception e) {
            outputArea.append("Connection failed: " + e.getMessage());
        }
    }

    private void bookMeeting() {
        try {
            String doctor = doctorField.getText();
            String patient = patientField.getText();
            String priority = priorityBox.getSelectedItem().toString();

            if (doctor.isEmpty() || patient.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "All fields required");
                return;
            }

            MeetingRequest req =
                    new MeetingRequest(doctor, patient, priority);

            String response = service.processBooking(req);

            outputArea.append(response + "\n");

            doctorField.setText("");
            patientField.setText("");

        } catch (Exception e) {
            outputArea.append("Booking error: " + e.getMessage() + "\n");
        }
    }

    private void loadEmergencies() {
        try {
            outputArea.append("\n--- Emergency Meetings ---\n");

            List<MeetingRequest> list =
                    service.getHighPriorityMeetings();

            if (list.isEmpty()) {
                outputArea.append("No emergencies.\n");
            } else {
                list.forEach(m ->
                        outputArea.append(m + "\n"));
            }

        } catch (Exception e) {
            outputArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void notifyClient(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame,
                    message,
                    "🚨 Emergency Alert",
                    JOptionPane.WARNING_MESSAGE);

            outputArea.append("\n[ALERT RECEIVED] " + message + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new DoctorMeetingSwingClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}