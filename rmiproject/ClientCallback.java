package DoctorAppointmentManager.rmiproject;
import java.rmi.*;

public interface ClientCallback extends Remote {
    void notifyClient(String message) throws RemoteException;
}