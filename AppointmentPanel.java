package DoctorAppointmentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AppointmentPanel extends JPanel implements ActionListener {

    JComboBox<String> doctorBox, patientBox;
    JTextField dateField, timeField;
    JButton addBtn, loadBtn;

    JTextArea displayArea;

    public AppointmentPanel() {
        setLayout(null);

        JLabel title = new JLabel("Book Appointment");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(150, 10, 300, 30);
        add(title);

        JLabel d = new JLabel("Doctor:");
        d.setBounds(40, 70, 120, 25);
        add(d);

        doctorBox = new JComboBox<>();
        doctorBox.setBounds(150, 70, 250, 25);
        add(doctorBox);

        JLabel p = new JLabel("Patient:");
        p.setBounds(40, 110, 120, 25);
        add(p);

        patientBox = new JComboBox<>();
        patientBox.setBounds(150, 110, 250, 25);
        add(patientBox);

        JLabel dt = new JLabel("Date (YYYY-MM-DD):");
        dt.setBounds(40, 150, 150, 25);
        add(dt);

        dateField = new JTextField();
        dateField.setBounds(200, 150, 200, 25);
        add(dateField);

        JLabel tm = new JLabel("Time (HH:MM):");
        tm.setBounds(40, 190, 150, 25);
        add(tm);

        timeField = new JTextField();
        timeField.setBounds(200, 190, 200, 25);
        add(timeField);

        addBtn = new JButton("Book Appointment");
        addBtn.setBounds(140, 240, 200, 35);
        addBtn.addActionListener(this);
        add(addBtn);

        loadBtn = new JButton("Load Appointments");
        loadBtn.setBounds(140, 290, 200, 35);
        loadBtn.addActionListener(this);
        add(loadBtn);

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(displayArea);
        scroll.setBounds(20, 340, 420, 220);
        add(scroll);

        loadDoctorPatientLists();
    }

    // LOAD DOCTORS & PATIENTS FROM DB
    private void loadDoctorPatientLists() {
        try {
            Database.connect();

            doctorBox.removeAllItems();
            patientBox.removeAllItems();

            List<Doctor> docs = Database.getAllDoctors();
            for (Doctor d : docs) {
                doctorBox.addItem(d.getId() + " - " + d.getName());
            }

            List<Patient> pats = Database.getAllPatients();
            for (Patient p : pats) {
                patientBox.addItem(p.getId() + " - " + p.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // HANDLE ADD / LOAD BUTTONS
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            bookAppointment();
        } else if (e.getSource() == loadBtn) {
            loadAppointments();
        }
    }

    // SAVE APPOINTMENT TO DATABASE
    private void bookAppointment() {
        try {
            String selectedDoctor = (String) doctorBox.getSelectedItem();
            String selectedPatient = (String) patientBox.getSelectedItem();

            if (selectedDoctor == null || selectedPatient == null) {
                JOptionPane.showMessageDialog(this, "No doctor or patient selected.");
                return;
            }

            int doctorId = Integer.parseInt(selectedDoctor.split(" - ")[0]);
            int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);

            String date = dateField.getText().trim();
            String time = timeField.getText().trim();

            if (date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter date and time.");
                return;
            }

            Database.addAppointment(doctorId, patientId, date + " " + time);
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // LOAD APPOINTMENT DATA
    private void loadAppointments() {
        List<String> list = Database.getAppointments();

        displayArea.setText("");
        for (String s : list) {
            displayArea.append(s + "\n");
        }
    }
}
