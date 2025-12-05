package DoctorAppointmentManager;

public class Person {
    protected String name;
    protected int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return name + " (Age: " + age + ")";
    }
}
