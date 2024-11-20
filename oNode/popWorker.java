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
                    String nextIP = routs.routs.get(msg[1]).previousIP;

                    this.flows.addFlow(msg[1], clientAddress.getHostAddress(), nextIP);

                    Socket socket1 = new Socket(nextIP, 8090);
                    DataOutputStream out = new DataOutputStream(socket1.getOutputStream());

                    out.writeUTF("FLOW");
                    out.writeUTF(msg[1]);

                    socket1.close();
                } else if (msg[0].equals("END")) {
                    String targetServer = msg[1];

                    for (Flow f : this.flows.flows) {
                        if (f.source.equals(targetServer)) {
                            f.targets.remove(clientAddress.getHostAddress());
                            if (f.targets.isEmpty()) {
                                String nextIP = routs.routs.get(targetServer).previousIP;

                                Socket socket1 = new Socket(nextIP, 8090);
                                DataOutputStream out = new DataOutputStream(socket1.getOutputStream());

                                out.writeUTF("END");
                                out.writeUTF(targetServer);

                                socket1.close();
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
