import java.net.*;

public class DoctorMulticastReceiver {

    public static void main(String[] args) throws Exception {

        InetAddress group = InetAddress.getByName("230.0.0.0");
        MulticastSocket socket = new MulticastSocket(4446);
        socket.joinGroup(group);

        System.out.println("Doctor Notification Listener Started...");

        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Notification: " + msg);
        }
    }
}
