package DoctorAppointmentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI extends JFrame implements ActionListener {

    JTextField username;
    JPasswordField password;
    JButton loginBtn;

    public LoginGUI() {
        if (Database.con == null) {
            Database.connect();
        }

        setTitle("Doctor Appointment - Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(130, 20, 200, 30);
        add(title);

        JLabel u = new JLabel("Username:");
        u.setBounds(50, 70, 100, 25);
        add(u);

        username = new JTextField();
        username.setBounds(150, 70, 180, 25);
        add(username);

        JLabel p = new JLabel("Password:");
        p.setBounds(50, 110, 100, 25);
        add(p);

        password = new JPasswordField();
        password.setBounds(150, 110, 180, 25);
        add(password);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(140, 160, 120, 30);
        loginBtn.addActionListener(this);
        add(loginBtn);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = username.getText();
        String pass = new String(password.getPassword());

        if (user.equals("admin") && pass.equals("password123")) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            dispose();
            new DashboardGUI();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials!");
        }
    }
}
