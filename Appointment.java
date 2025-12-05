package DoctorAppointmentManager;

public class Appointment {
    private int token;
    private String doctorName;
    private String patientName;
    private String dateTime; // simple string for demo

    public Appointment(int token, String doctorName, String patientName, String dateTime) {
        this.token = token;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.dateTime = dateTime;
    }

    public int getToken() { return token; }
    public String getDoctorName() { return doctorName; }
    public String getPatientName() { return patientName; }
    public String getDateTime() { return dateTime; }

    @Override
    public String toString() {
        return token + " | Dr: " + doctorName + " | Patient: " + patientName + " | " + dateTime;
    }
}
