package DoctorAppointmentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddPatientPanel extends JPanel implements ActionListener {
    JTextField nameField, ageField, issueField;
    JButton addBtn;

    public AddPatientPanel() {
        setLayout(null);

        JLabel title = new JLabel("Add Patient");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(40, 10, 300, 30);
        add(title);

        JLabel n = new JLabel("Name:");
        n.setBounds(40, 60, 80, 25);
        add(n);
        nameField = new JTextField();
        nameField.setBounds(130, 60, 200, 25);
        add(nameField);

        JLabel a = new JLabel("Age:");
        a.setBounds(40, 100, 80, 25);
        add(a);
        ageField = new JTextField();
        ageField.setBounds(130, 100, 200, 25);
        add(ageField);

        JLabel i = new JLabel("Issue:");
        i.setBounds(40, 140, 80, 25);
        add(i);
        issueField = new JTextField();
        issueField.setBounds(130, 140, 200, 25);
        add(issueField);

        addBtn = new JButton("Add Patient");
        addBtn.setBounds(130, 190, 140, 35);
        addBtn.addActionListener(this);
        add(addBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Ensure DB connection
        Database.connect();

        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String issue = issueField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || issue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0) throw new NumberFormatException("Age must be positive");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric age.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Database.addPatient(name, age, issue);
            JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // clear fields
            nameField.setText("");
            ageField.setText("");
            issueField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding patient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
