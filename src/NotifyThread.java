import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class NotifyThread extends Thread {

    private int maxSizePackage;
    private byte[] buf;
    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    private int timeout = 10000;
    private int j;

    public NotifyThread(InetAddress address, int port, DatagramSocket socket, int maxSizePackage, int j) throws Exception {
        this.address = address;
        this.port = port;
        this.maxSizePackage = maxSizePackage;
        this.socket = socket;
        this.j = j;
    }

    @Override
    public void run() {
        for (int i = 1; ; ) {

            try {
                byte[] message = "notify".getBytes();
                buf = new byte[maxSizePackage];
                DatagramPacket packetToClient = new DatagramPacket(message, message.length,
                        address, port);
                DatagramPacket packetFromClient = new DatagramPacket(buf, buf.length);
//                System.out.println("Я отправил нотифай");
                socket.send(packetToClient);
                socket.setSoTimeout(timeout);
//                System.out.println("Я жду гета");
                socket.receive(packetFromClient);
//                System.out.println("Я получил гет");
                i++;

                System.out.println(LocalDateTime.now() + ", " + j + " Client: " + new String(packetFromClient.getData()) + ", i = " + i);
                writeOnFile(LocalDateTime.now() + ", " + j + " Client: " + new String(packetFromClient.getData()) + ", i = " + i);

            } catch (Exception e) {
//                writeOnFile(LocalDateTime.now() + ", " + e.toString() + ", i = " + i);
                Thread.currentThread().destroy();
            }
            try {
                Thread.sleep((long)(30 + Math.random() * 90) * 1000);
            } catch (InterruptedException e) {

            }
        }
    }

    private void writeOnFile(String str) {
        try (FileWriter myWriter = new FileWriter("log.csv", true)) {
            myWriter.write(str + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
