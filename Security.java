package DoctorAppointmentManager;

import javax.swing.*;

public class Security {
    public static boolean verify(JFrame parent) {
        String answer = JOptionPane.showInputDialog(parent, "Security Check:\nWhat is the text shown here?: xcdf00");
        return answer != null && answer.trim().equals("xcdf00");
    }
}
