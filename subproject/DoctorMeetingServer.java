import java.net.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class DoctorMeetingServer {

    static List<String> meetings = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Doctor Meeting Server started on port 5000");

        while (true) {
            Socket socket = serverSocket.accept();

            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String doctor = in.readLine();
                String date = in.readLine();
                String time = in.readLine();

                // Validation
                LocalDate meetingDate = LocalDate.parse(date);
                LocalTime meetingTime = LocalTime.parse(time);

                if (meetingDate.isBefore(LocalDate.now())) {
                    out.println("Invalid date. Cannot book past meetings.");
                    socket.close();
                    return;
                }

                String meeting = doctor + " | " + date + " | " + time;

                if (meetings.contains(meeting)) {
                    out.println("Meeting already exists.");
                } else {
                    meetings.add(meeting);
                    out.println("Meeting scheduled successfully!");

                    // Send multicast notification
                    DoctorMulticastSender.sendNotification(
                            "New Meeting: " + meeting);
                }

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
