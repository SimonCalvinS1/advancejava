package DoctorAppointmentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorTablePanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    JButton refreshBtn;

    public DoctorTablePanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Doctor Records", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.addActionListener(e -> loadDoctors());
        topPanel.add(refreshBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        String[] cols = { "Doctor ID", "Name", "Age", "Specialization" };
        model = new DefaultTableModel(cols, 0);

        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        loadDoctors(); // load initial dataset
    }

    /** Fetch doctors from DB and populate JTable */
    void loadDoctors() {
        model.setRowCount(0);

        List<Doctor> list = Database.getAllDoctors();

        for (Doctor d : list) {
            model.addRow(new Object[] {
                    d.getId(),
                    d.getName(),
                    d.getAge(),
                    d.getSpecialization()
            });
        }
    }
}
