import java.net.*;
import java.io.*;
import java.util.Scanner;

public class DoctorMeetingClient {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        Socket socket = new Socket("localhost", 5000);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        System.out.print("Enter Doctor Name: ");
        String doctor = sc.nextLine();

        System.out.print("Enter Meeting Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Enter Meeting Time (HH:MM): ");
        String time = sc.nextLine();

        out.println(doctor);
        out.println(date);
        out.println(time);

        System.out.println("Server Response: " + in.readLine());

        socket.close();
        sc.close();
    }
}
