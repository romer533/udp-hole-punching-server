import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class UPDHolePunchingServer {

    public static void main(String[] args) throws Exception {

        int maxSizePackage = 8;
        byte[] buf;
        InetAddress address = InetAddress.getByName("0.0.0.0");
        int port = 25147;
        int timeout = 30000;
        DatagramSocket socket = new DatagramSocket(port);

        writeOnFile(LocalDateTime.now() + ", " + "Server start");

        for (int i = 1;;) {

            if (i % 10 != 0) {
                try {
                    byte[] responsePong = "pong".getBytes();
                    buf = new byte[maxSizePackage];
                    DatagramPacket packetPingFromClient = new DatagramPacket(buf, buf.length);
                    socket.setSoTimeout(timeout);
                    socket.receive(packetPingFromClient);
                    i++;
                    address = packetPingFromClient.getAddress();
                    port = packetPingFromClient.getPort();

                    System.out.println(LocalDateTime.now() + ", Client: " + new String(packetPingFromClient.getData()) + ", i = " + i);
                    writeOnFile(LocalDateTime.now() + ", Client: " + new String(packetPingFromClient.getData()) + ", i = " + i);

                    DatagramPacket packetPongToClient = new DatagramPacket(responsePong, responsePong.length,
                            address, port);
                    socket.send(packetPongToClient);

                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception e) {
                    System.out.println(LocalDateTime.now() + ", " + e.toString() + ", i = " + i);
                    writeOnFile(LocalDateTime.now() + ", " + e.toString() + ", i = " + i);
                    e.printStackTrace();
                }
            } else {
                try {
                    byte[] responseGet = "notify".getBytes();
                    buf = new byte[maxSizePackage];
                    DatagramPacket packetNotifyGetFromClient = new DatagramPacket(buf, buf.length);
                    DatagramPacket packetNotifyToClient = new DatagramPacket(responseGet, responseGet.length,
                            address, port);
                    socket.send(packetNotifyToClient);
                    socket.setSoTimeout(timeout);
                    socket.receive(packetNotifyGetFromClient);
                    i++;

                    System.out.println(LocalDateTime.now() + ", Client: " + new String(packetNotifyGetFromClient.getData()) + ", i = " + i);
                    writeOnFile(LocalDateTime.now() + ", Client: " + new String(packetNotifyGetFromClient.getData()) + ", i = " + i);

                } catch (Exception e) {
                    System.out.println(LocalDateTime.now() + ", " + e.toString() + ", i = " + i);
                    writeOnFile(LocalDateTime.now() + ", " + e.toString() + ", i = " + i);
                    e.printStackTrace();
                }

            }
        }
    }

    public static void writeOnFile(String str) {
        try (FileWriter myWriter = new FileWriter("log.csv", true)) {
            myWriter.write(str + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
