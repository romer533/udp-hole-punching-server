import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PongThread extends Thread {

    private int maxSizePackage;
    private byte[] buf;
    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    private int j;
    private Thread notifyThread;
    ArrayList<InetAddress> oldAddresses = new ArrayList<>();
    ArrayList<Integer> oldPorts = new ArrayList<>();

    public PongThread(InetAddress address, int port, DatagramSocket socket, int maxSizePackage, int j) {
        this.address = address;
        this.port = port;
        this.socket = socket;
        this.maxSizePackage = maxSizePackage;
        this.j = j;
    }

    @Override
    public void run() {
        for (int i = 1; ; i++) {
            try {
                byte[] responsePong = "pong".getBytes();
                buf = new byte[maxSizePackage];
                DatagramPacket packetPingFromClient = new DatagramPacket(buf, buf.length);
                System.out.println("Я жду пинга");
                socket.receive(packetPingFromClient);
                System.out.println("Я получил пинг");
                if (!oldAddresses.contains(address)) oldAddresses.add(address);
                if (!oldPorts.contains(port)) oldPorts.add(port);
                address = packetPingFromClient.getAddress();
                port = packetPingFromClient.getPort();

                System.out.println(LocalDateTime.now() + ", Client: " + new String(packetPingFromClient.getData()) + ", i = " + i);
//                writeOnFile(LocalDateTime.now() + ", Client: " + new String(packetPingFromClient.getData()) + ", i = " + i);

                DatagramPacket packetPongToClient = new DatagramPacket(responsePong, responsePong.length,
                        address, port);
                System.out.println("Я отправил понг");
                socket.send(packetPongToClient);

                if (i != 1 && (!oldAddresses.contains(address) || !oldPorts.contains(port))) {
                    i = 1;
                }


                if (i == 1) {
                    notifyThread = new NotifyThread(address, port, socket, maxSizePackage, j, Thread.currentThread());
                    notifyThread.start();
                System.out.println("Я запустил поток");
                    j++;
                    Thread.sleep(1000);
                }
            } catch (Exception e) {}
        }
    }
}
