package DoctorAppointmentManager;

import java.sql.*;
import java.util.ArrayList;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/doctor_appointment";
    private static final String USER = "newuser";
    private static final String PASS = "password123";

    protected static Connection con;

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✓ Connected to MySQL successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addDoctor(String name, int age, String specialization) {
        try {
            String q = "INSERT INTO doctors(name, age, specialization) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, specialization);
            ps.executeUpdate();
            System.out.println("Doctor added!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- ADD PATIENT -------------------
    public static void addPatient(String name, int age, String issue) {
        try {
            String q = "INSERT INTO patients(name, age, issue) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, issue);
            ps.executeUpdate();
            System.out.println("Patient added!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- ADD APPOINTMENT -------------------
    public static void addAppointment(int doctorId, int patientId, String dateTime) {
        try {
            String q = "INSERT INTO appointments(token, doctor_id, patient_id, date_time) VALUES (?, ?, ?, ?)";

            // token = automatic here (or set manually)
            PreparedStatement ps = con.prepareStatement(q);
            ps.setInt(1, generateToken());
            ps.setInt(2, doctorId);
            ps.setInt(3, patientId);
            ps.setString(4, dateTime);

            ps.executeUpdate();
            System.out.println("Appointment booked!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- GENERATE UNIQUE TOKEN -------------------
    private static int generateToken() {
        try {
            String q = "SELECT COALESCE(MAX(token), 100) + 1 FROM appointments";
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 101;
    }

    // ----------------- READ DOCTORS -------------------
    public static ArrayList<Doctor> getAllDoctors() {
        ArrayList<Doctor> list = new ArrayList<>();
        try {
            String q = "SELECT * FROM doctors";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(q);

            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("specialization")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ----------------- READ PATIENTS -------------------
    public static ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> list = new ArrayList<>();
        try {
            String q = "SELECT * FROM patients";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(q);

            while (rs.next()) {
                list.add(new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("issue")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ----------------- READ APPOINTMENTS -------------------
    public static ArrayList<String> getAppointments() {
        ArrayList<String> list = new ArrayList<>();
        try {
            String q = """
                        SELECT a.token, d.name AS doctor, p.name AS patient, a.date_time
                        FROM appointments a
                        JOIN doctors d ON a.doctor_id = d.doctor_id
                        JOIN patients p ON a.patient_id = p.patient_id
                        ORDER BY a.token
                    """;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(q);

            while (rs.next()) {
                list.add("Token: " + rs.getInt("token") +
                        " | Doctor: " + rs.getString("doctor") +
                        " | Patient: " + rs.getString("patient") +
                        " | Date: " + rs.getString("date_time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ----------------- UPDATE DOCTOR -------------------
    public static void updateDoctor(int id, String newName, int newAge, String newSpec) {
        try {
            String q = "UPDATE doctors SET name=?, age=?, specialization=? WHERE doctor_id=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, newName);
            ps.setInt(2, newAge);
            ps.setString(3, newSpec);
            ps.setInt(4, id);
            ps.executeUpdate();
            System.out.println("Doctor updated!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- DELETE DOCTOR -------------------
    public static void deleteDoctor(int id) {
        try {
            String q = "DELETE FROM doctors WHERE doctor_id=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Doctor deleted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- DELETE PATIENT -------------------
    public static void deletePatient(int id) {
        try {
            String q = "DELETE FROM patients WHERE patient_id=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Patient deleted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- CLOSE CONNECTION -------------------
    public static void close() {
        try {
            if (con != null)
                con.close();
            System.out.println("Connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
