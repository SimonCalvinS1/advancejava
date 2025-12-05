package DoctorAppointmentManager;

import javax.swing.SwingUtilities;

public class DoctorAppointmentMain {
   public static void main(String[] args) {
      Database.connect();
      SwingUtilities.invokeLater(() -> {
         new LoginGUI();
      });
   }
}
