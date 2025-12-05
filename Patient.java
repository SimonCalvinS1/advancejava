package DoctorAppointmentManager;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String issue;

    public Patient(int id, String name, int age, String issue) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.issue = issue;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getIssue() { return issue; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + issue;
    }
}
