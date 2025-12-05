package DoctorAppointmentManager;

import java.sql.*;
import java.util.Scanner;

public class TestF {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String url = "jdbc:mysql://localhost:3306/doctor_appointment";
        String user = "newuser";
        String pass = "password123";

        try {
            Connection con = DriverManager.getConnection(url, user, pass);

            while (true) {
                System.out.println("\n1. Add Employee");
                System.out.println("2. View Employees");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                sc.nextLine();

                if (ch == 1) {
                    System.out.print("Enter Employee ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    String q = "INSERT INTO employee VALUES (?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(q);
                    ps.setInt(1, id);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.executeUpdate();

                    System.out.println("Employee added.");
                }

                else if (ch == 2) {
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM employee");

                    while (rs.next()) {
                        System.out.println(
                                rs.getInt(1) + " | " +
                                        rs.getString(2) + " | " +
                                        rs.getString(3));
                    }
                }

                else if (ch == 3) {
                    System.out.println("Exiting...");
                    break;
                }

                else {
                    System.out.println("Invalid choice.");
                }
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sc.close();
    }
}
