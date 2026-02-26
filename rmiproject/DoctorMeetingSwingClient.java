package DoctorAppointmentManager.rmiproject;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.util.List;

public class DoctorMeetingSwingClient extends JFrame {

    private RemoteService service;

    private JTextField doctorField = new JTextField(15);
    private JTextField patientField = new JTextField(15);
    private JComboBox<String> priorityBox =
            new JComboBox<>(new String[]{"Emergency", "Routine", "Follow-up"});

    private JTextArea outputArea = new JTextArea();

    public DoctorMeetingSwingClient() {

        setTitle("Hospital Remote Booking System");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        connectToServer();

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("New Booking"));

        form.add(new JLabel("Doctor Name:"));
        form.add(doctorField);

        form.add(new JLabel("Patient Name:"));
        form.add(patientField);

        form.add(new JLabel("Priority:"));
        form.add(priorityBox);

        JButton bookBtn = new JButton("Book Meeting");
        JButton emergencyBtn = new JButton("View Emergencies");

        form.add(bookBtn);
        form.add(emergencyBtn);

        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);

        add(form, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        bookBtn.addActionListener(e -> bookMeeting());
        emergencyBtn.addActionListener(e -> loadEmergencies());

        setVisible(true);
    }

    private void connectToServer() {
        try {
            service = (RemoteService) Naming.lookup("rmi://localhost/DoctorService");
            outputArea.append("Connected to: " + service.getServerStatus() + "\n\n");
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
                JOptionPane.showMessageDialog(this, "All fields required");
                return;
            }

            MeetingRequest req = new MeetingRequest(doctor, patient, priority);
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
            outputArea.append("\n--- EMERGENCY MEETINGS ---\n");

            List<MeetingRequest> list = service.getHighPriorityMeetings();

            if (list.isEmpty()) {
                outputArea.append("No emergency meetings.\n");
            } else {
                list.forEach(m -> outputArea.append(m + "\n"));
            }

        } catch (Exception e) {
            outputArea.append("Fetch error: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoctorMeetingSwingClient::new);
    }
}