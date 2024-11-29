package oNode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class popWorker implements Runnable {
    private Routs routs;
    private Flows flows;

    public popWorker(Routs routs, Flows flows) {
        this.routs = routs;
        this.flows = flows;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(8070);
            System.out.println("Servidor UDP à escuta na porta 8070");

            // TODO: Implementar threads

            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                String[] msg = message.split(",");

                if (msg[0].equals("PING")) {
                    byte[] response = "PONG".getBytes();
                    packet = new DatagramPacket(response, response.length, clientAddress, clientPort);
                    socket.send(packet);
                } else if (msg[0].equals("FLOW")) {
                    String videoId = msg[2];
                    String targetServer = msg[1];

                    String nextIP = routs.routs.get(targetServer).previousIP;

                    this.flows.addFlow(videoId, targetServer, clientAddress.getHostAddress(), nextIP);

                    Socket socket1 = new Socket(nextIP, 8090);
                    DataOutputStream out = new DataOutputStream(socket1.getOutputStream());

                    out.writeUTF("FLOW");
                    out.writeUTF(videoId);
                    out.writeUTF(targetServer);

                    socket1.close();
                } else if (msg[0].equals("END")) {
                    String videoId = msg[2];
                    String targetServer = msg[1];

                    Flow f = this.flows.flows.get(videoId);
                    f.targets.remove(clientAddress.getHostAddress());
                    if (f.targets.isEmpty()) {
                        String nextIP = routs.routs.get(targetServer).previousIP;

                        Socket socket1 = new Socket(nextIP, 8090);
                        DataOutputStream out = new DataOutputStream(socket1.getOutputStream());

                        out.writeUTF("END");
                        out.writeUTF(videoId);
                        out.writeUTF(targetServer);

                        socket1.close();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
