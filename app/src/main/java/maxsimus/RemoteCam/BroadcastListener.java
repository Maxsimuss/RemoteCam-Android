package maxsimus.RemoteCam;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastListener {
    public void startListeningToBroadcast(ServerFoundCallback callback) {
        new Thread(() -> {
            while (true) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000); // letting the cpu relax while we are unable to receive on the socket for some reason.

                    DatagramSocket socket = new DatagramSocket(43922, InetAddress.getByName("255.255.255.255"));
                    socket.setBroadcast(true);

                    byte[] buffer = new byte[128];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);


                    String message = new String(packet.getData()).trim();

                    if (message.equals("RemoteCam")) {
                        byte[] senderIP = packet.getAddress().getAddress();
                        int address = (senderIP[0] & 0xFF) | (senderIP[1] & 0xFF) << 8 | (senderIP[2] & 0xFF) << 16 | (senderIP[3] & 0xFF) << 24;

                        callback.serverFound(address);
                    }

                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }).start();
    }
}
