package DoctorAppointmentManager.rmiproject;
import java.rmi.Naming;
import java.util.Scanner;

public class DoctorMeetingClient {
    public static void main(String[] args) {
        try {
            RemoteService service = (RemoteService) Naming.lookup("rmi://localhost/DoctorService");
            Scanner sc = new Scanner(System.in);

            System.out.println("--- HOSPITAL REMOTE PORTAL ---");
            System.out.println(service.getServerStatus());

            while (true) {
                System.out.println("\n1. New Booking  2. View Emergencies  3. Exit");
                int choice = Integer.parseInt(sc.nextLine());

                if (choice == 1) {
                    System.out.print("Doctor: "); String d = sc.nextLine();
                    System.out.print("Patient: "); String p = sc.nextLine();
                    System.out.print("Priority (Emergency/Routine): "); String pr = sc.nextLine();
                    
                    MeetingRequest req = new MeetingRequest(d, p, pr);
                    System.out.println(service.processBooking(req));
                } else if (choice == 2) {
                    service.getHighPriorityMeetings().forEach(System.out::println);
                } else if (choice == 3) break;
            }
        } catch (Exception e) {
            System.err.println("Client Error: " + e.getMessage());
        }
    }
}