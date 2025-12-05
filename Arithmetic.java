package DoctorAppointmentManager;

@FunctionalInterface
public interface Arithmetic {
    double operation(double a, double b);
}

@FunctionalInterface
interface StringOp {
    String apply(String s);
}

@FunctionalInterface
interface NumberCheck {
    boolean test(int n);
}
