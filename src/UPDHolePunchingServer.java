import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class UPDHolePunchingServer {

    public static void main(String[] args) throws Exception {

        int maxSizePackage = 8;
        byte[] buf;
        InetAddress address = InetAddress.getByName("0.0.0.0");
        InetAddress oldAddress;
        int port = 8080;
        DatagramSocket socket = new DatagramSocket(port);

        Thread notifyThread;

        writeOnFile(LocalDateTime.now() + ", " + "Server start");

        for (int i = 1; ; i++) {

//            if (i % 10 != 0) {
            try {
                byte[] responsePong = "pong".getBytes();
                buf = new byte[maxSizePackage];
                DatagramPacket packetPingFromClient = new DatagramPacket(buf, buf.length);
//                System.out.println("Я жду пинга");
                socket.receive(packetPingFromClient);
//                System.out.println("Я получил пинг");
                oldAddress = address;
                address = packetPingFromClient.getAddress();
                port = packetPingFromClient.getPort();

                System.out.println(LocalDateTime.now() + ", Client: " + new String(packetPingFromClient.getData()) + ", i = " + i);
                writeOnFile(LocalDateTime.now() + ", Client: " + new String(packetPingFromClient.getData()) + ", i = " + i);

                DatagramPacket packetPongToClient = new DatagramPacket(responsePong, responsePong.length,
                        address, port);
//                System.out.println("Я отправил понг");
                socket.send(packetPongToClient);

                if (i != 1 && !oldAddress.equals(address)) {
                    i = 1;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
//            }
//            else {
//                try {
//                    byte[] responseGet = "notify".getBytes();
//                    buf = new byte[maxSizePackage];
//                    DatagramPacket packetNotifyGetFromClient = new DatagramPacket(buf, buf.length);
//                    DatagramPacket packetNotifyToClient = new DatagramPacket(responseGet, responseGet.length,
//                            address, port);
//                    socket.send(packetNotifyToClient);
//                    socket.setSoTimeout(timeout);
//                    socket.receive(packetNotifyGetFromClient);
//                    i++;
//
//                    System.out.println(LocalDateTime.now() + ", Client: " + new String(packetNotifyGetFromClient.getData()) + ", i = " + i);
//                    writeOnFile(LocalDateTime.now() + ", Client: " + new String(packetNotifyGetFromClient.getData()) + ", i = " + i);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }

            if (i == 1) {
                notifyThread = new NotifyThread(address, port, socket, maxSizePackage);
                notifyThread.start();
//                System.out.println("Я запустил поток");
                Thread.sleep(1000);
            }

        }
    }

    private static void writeOnFile(String str) {
        try (FileWriter myWriter = new FileWriter("log.csv", true)) {
            myWriter.write(str + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
