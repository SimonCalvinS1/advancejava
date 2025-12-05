package DoctorAppointmentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientTablePanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    JButton refreshBtn;

    public PatientTablePanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Patient Records", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        // Add refresh button beside title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.addActionListener(e -> loadPatients());
        topPanel.add(refreshBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        String[] cols = { "Patient ID", "Name", "Age", "Issue" };
        model = new DefaultTableModel(cols, 0);

        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        loadPatients(); // load only once at initial display
    }

    /** Fetch patients from DB and populate JTable */
    void loadPatients() {
        model.setRowCount(0);

        List<Patient> list = Database.getAllPatients();

        for (Patient p : list) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    p.getAge(),
                    p.getIssue()
            });
        }
    }
}
