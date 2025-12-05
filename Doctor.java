package DoctorAppointmentManager;

public class Doctor {
    private int id;
    private String name;
    private int age;
    private String specialization;

    public Doctor(int id, String name, int age, String specialization) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.specialization = specialization;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getSpecialization() { return specialization; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + specialization;
    }
}
