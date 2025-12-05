package DoctorAppointmentManager;

import javax.swing.*;
import java.awt.*;

public class DashboardGUI extends JFrame {

    public DashboardGUI() {
        setTitle("Doctor Appointment System - Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Add Doctor", new AddDoctorPanel());
        tabs.addTab("Add Patient", new AddPatientPanel());
        tabs.addTab("Appointments", new AppointmentPanel());
        tabs.addTab("Doctor List", new DoctorTablePanel());
        tabs.addTab("Patient List", new PatientTablePanel());
        tabs.addTab("Lambda & Streams", new LambdaStreamPanel());

        add(tabs);

        setVisible(true);
    }
}
