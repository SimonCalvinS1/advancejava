package DoctorAppointmentManager.rmiproject;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.net.InetAddress;

public class DoctorMeetingRMI extends UnicastRemoteObject implements RemoteService {

    private final List<MeetingRequest> database =
            Collections.synchronizedList(new ArrayList<>());

    private final List<ClientCallback> clients =
            Collections.synchronizedList(new ArrayList<>());

    public DoctorMeetingRMI() throws RemoteException { super(); }

    @Override
    public synchronized String processBooking(MeetingRequest req) throws RemoteException {

        database.add(req);

        System.out.println(">>> SERVER LOG: Processed " + req.priority +
                " for " + req.patientName);

        if (req.priority.equalsIgnoreCase("Emergency")) {
            notifyAllClients("🚨 EMERGENCY ALERT: " + req.patientName +
                    " scheduled with Dr." + req.doctorName);
        }

        return "CONFIRMED: Meeting added. Fee: $" + req.estimatedFee;
    }

    private void notifyAllClients(String msg) {
        for (ClientCallback client : clients) {
            try {
                client.notifyClient(msg);
            } catch (Exception e) {
                System.out.println("Failed to notify a client.");
            }
        }
    }

    @Override
    public List<MeetingRequest> getHighPriorityMeetings() {
        return database.stream()
                .filter(m -> m.priority.equalsIgnoreCase("Emergency"))
                .toList();
    }

    @Override
    public String getServerStatus() {
        try {
            return "Server Node: " +
                    InetAddress.getLocalHost().getHostName() +
                    " | Active Records: " + database.size();
        } catch (Exception e) {
            return "Status Unknown";
        }
    }

    @Override
    public void registerClient(ClientCallback client) {
        clients.add(client);
        System.out.println("Client registered.");
    }

    @Override
    public void unregisterClient(ClientCallback client) {
        clients.remove(client);
        System.out.println("Client unregistered.");
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("DoctorService", new DoctorMeetingRMI());
            System.out.println("MED-RMI Server running with Callback enabled...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}