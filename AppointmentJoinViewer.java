package DoctorAppointmentManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AppointmentJoinViewer {

    private static final String URL = "jdbc:mysql://localhost:3306/doctor_appointment";
    private static final String USER = "newuser";
    private static final String PASS = "password123";

    public static void main(String[] args) {
        try {
            // Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✓ Connected to MySQL\n");

            // JOIN Query
            String query = """
                    SELECT a.token,
                           d.name AS doctor_name,
                           d.specialization,
                           p.name AS patient_name,
                           p.issue,
                           a.date_time
                    FROM appointments a
                    JOIN doctors d ON a.doctor_id = d.doctor_id
                    JOIN patients p ON a.patient_id = p.patient_id
                    ORDER BY a.token;
                    """;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("---- Appointment JOIN Result ----\n");

            while (rs.next()) {
                System.out.println(
                        "Token       : " + rs.getInt("token") +
                        "\nDoctor      : " + rs.getString("doctor_name") +
                        "\nSpecialization : " + rs.getString("specialization") +
                        "\nPatient     : " + rs.getString("patient_name") +
                        "\nIssue       : " + rs.getString("issue") +
                        "\nDate & Time : " + rs.getString("date_time") +
                        "\n-----------------------------------------"
                );
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
