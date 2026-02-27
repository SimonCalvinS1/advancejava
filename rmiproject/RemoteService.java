package DoctorAppointmentManager.rmiproject;
import java.rmi.*;
import java.util.List;

public interface RemoteService extends Remote {

    String processBooking(MeetingRequest request) throws RemoteException;

    List<MeetingRequest> getHighPriorityMeetings() throws RemoteException;

    String getServerStatus() throws RemoteException;

    void registerClient(ClientCallback client) throws RemoteException;

    void unregisterClient(ClientCallback client) throws RemoteException;
}