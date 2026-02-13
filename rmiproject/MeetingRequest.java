package DoctorAppointmentManager.rmiproject;
import java.io.Serializable;

public class MeetingRequest implements Serializable {
    public String doctorName;
    public String patientName;
    public String priority; // Emergency, Routine, Follow-up
    public double estimatedFee;

    public MeetingRequest(String d, String p, String pr) {
        this.doctorName = d;
        this.patientName = p;
        this.priority = pr;
        // Logic: Emergencies cost more
        this.estimatedFee = pr.equalsIgnoreCase("Emergency") ? 500.0 : 150.0;
    }

    @Override
    public String toString() {
        return String.format("[%s] Patient: %-10s | Doctor: %-10s | Fee: $%.2f", 
                priority.toUpperCase(), patientName, doctorName, estimatedFee);
    }
}