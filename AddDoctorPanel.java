package DoctorAppointmentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddDoctorPanel extends JPanel implements ActionListener {

    JTextField name, age, spec;
    JButton addBtn;

    public AddDoctorPanel() {
        setLayout(null);

        JLabel t = new JLabel("Add Doctor");
        t.setFont(new Font("Arial", Font.BOLD, 22));
        t.setBounds(150, 20, 200, 30);
        add(t);

        JLabel n = new JLabel("Name:");
        n.setBounds(50, 80, 100, 25);
        add(n);

        name = new JTextField();
        name.setBounds(150, 80, 200, 25);
        add(name);

        JLabel a = new JLabel("Age:");
        a.setBounds(50, 120, 100, 25);
        add(a);

        age = new JTextField();
        age.setBounds(150, 120, 200, 25);
        add(age);

        JLabel s = new JLabel("Specialization:");
        s.setBounds(50, 160, 100, 25);
        add(s);

        spec = new JTextField();
        spec.setBounds(150, 160, 200, 25);
        add(spec);

        addBtn = new JButton("Add Doctor");
        addBtn.setBounds(150, 220, 150, 35);
        addBtn.addActionListener(this);
        add(addBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Ensure DB connection
        Database.connect();

        String docName = name.getText().trim();
        String ageText = age.getText().trim();
        String specialization = spec.getText().trim();

        if (docName.isEmpty() || ageText.isEmpty() || specialization.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int docAge;
        try {
            docAge = Integer.parseInt(ageText);
            if (docAge <= 0) throw new NumberFormatException("Age must be positive");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric age.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Database.addDoctor(docName, docAge, specialization);
            JOptionPane.showMessageDialog(this, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // clear fields
            name.setText("");
            age.setText("");
            spec.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding doctor: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
