import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class UPDHolePunchingServer {

    public static Thread pongThread = null;

    public static void main(String[] args) throws Exception {

        int maxSizePackage = 8;
        InetAddress address = InetAddress.getByName("0.0.0.0");
        int port = 8080;
        DatagramSocket socket = new DatagramSocket(port);
        int j = 0;

        pongThread = new PongThread(address, port, socket, maxSizePackage, j);
        pongThread.start();

        writeOnFile(LocalDateTime.now() + ", " + "Server start");


    }

    private static void writeOnFile(String str) {
        try (FileWriter myWriter = new FileWriter("log.csv", true)) {
            myWriter.write(str + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
