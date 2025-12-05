package DoctorAppointmentManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class LambdaStreamPanel extends JPanel {

    JTextArea output;

    public LambdaStreamPanel() {
        setLayout(null);

        JLabel t = new JLabel("Lambda & Stream Operations");
        t.setFont(new Font("Arial", Font.BOLD, 22));
        t.setBounds(80, 10, 400, 40);
        add(t);

        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBounds(20, 70, 440, 350);
        add(scroll);

        JButton btn = new JButton("Run Lambda & Stream Demo");
        btn.setBounds(110, 440, 250, 40);
        btn.addActionListener(e -> runDemo());
        add(btn);
    }

    private void runDemo() {
        StringBuilder sb = new StringBuilder();

        Database.connect();

        List<Patient> patients = Database.getAllPatients();
        List<Doctor> doctors = Database.getAllDoctors();

        sb.append("---- STREAM OPERATIONS ----\n\n");

        // COUNT PATIENTS
        sb.append("Total Patients: ").append(patients.size()).append("\n");
        sb.append("Total Doctors: ").append(doctors.size()).append("\n\n");

        // DISTINCT ISSUES
        long distinctIssues = patients.stream()
                .map(Patient::getIssue)
                .distinct()
                .count();

        sb.append("Distinct Patient Issues: ").append(distinctIssues).append("\n");

        // AVERAGE AGE OF PATIENTS
        double avgAge = patients.stream()
                .mapToInt(Patient::getAge)
                .average()
                .orElse(0);

        sb.append("Average Patient Age: ").append(avgAge).append("\n");

        // SORT DOCTORS BY NAME
        sb.append("\nSorted Doctors:\n");
        doctors.stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .forEach(d -> sb.append(" - ").append(d).append("\n"));

        // FILTER SENIOR PATIENTS
        sb.append("\nPatients above 50:\n");
        patients.stream()
                .filter(p -> p.getAge() > 50)
                .forEach(p -> sb.append(" - ").append(p).append("\n"));

        output.setText(sb.toString());
    }
}
