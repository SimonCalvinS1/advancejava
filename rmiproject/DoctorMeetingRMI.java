package DoctorAppointmentManager.rmiproject;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.net.InetAddress;

public class DoctorMeetingRMI extends UnicastRemoteObject implements RemoteService {
    private final List<MeetingRequest> database = Collections.synchronizedList(new ArrayList<>());

    public DoctorMeetingRMI() throws RemoteException { super(); }

    @Override
    public String processBooking(MeetingRequest req) throws RemoteException {
        database.add(req);
        System.out.println(">>> SERVER LOG: Processed " + req.priority + " for " + req.patientName);
        return "CONFIRMED: Meeting added to secure vault. Fee: $" + req.estimatedFee;
    }

    @Override
    public List<MeetingRequest> getHighPriorityMeetings() throws RemoteException {
        return database.stream()
                .filter(m -> m.priority.equalsIgnoreCase("Emergency"))
                .toList();
    }

    @Override
    public String getServerStatus() throws RemoteException {
        try {
            return "Server Node: " + InetAddress.getLocalHost().getHostName() + " | Load: " + database.size() + " active records.";
        } catch (Exception e) { return "Status Unknown"; }
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("DoctorService", new DoctorMeetingRMI());
            System.out.println("MED-RMI Server is active and listening on port 1099...");
        } catch (Exception e) { e.printStackTrace(); }
    }
}