package DoctorAppointmentManager.rmiproject;
import java.rmi.*;
import java.util.List;

public interface RemoteService extends Remote {
    // Returns a complex object response rather than just a string
    String processBooking(MeetingRequest request) throws RemoteException;
    
    // Server-side filtering
    List<MeetingRequest> getHighPriorityMeetings() throws RemoteException;
    
    // System health check (Network info)
    String getServerStatus() throws RemoteException;
}