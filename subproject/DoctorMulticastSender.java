import java.net.*;

public class DoctorMulticastSender {

    public static void sendNotification(String message) {
        try {
            InetAddress group = InetAddress.getByName("230.0.0.0");
            DatagramSocket socket = new DatagramSocket();

            byte[] data = message.getBytes();
            DatagramPacket packet =
                    new DatagramPacket(data, data.length, group, 4446);

            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
