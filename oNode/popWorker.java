package oNode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class popWorker implements Runnable {
    private Routs routs;

    public popWorker(Routs routs) {
        this.routs = routs;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(8070);
            System.out.println("Configuração carregada!");
            System.out.println("Servidor UDP à escuta na porta 8070");

            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                if (message.equals("PING")) {
                    byte[] response = "PONG".getBytes();
                    packet = new DatagramPacket(response, response.length, clientAddress, clientPort);
                    socket.send(packet);
                } else {
                    Rout rout = routs.routs.get(message);
                    String nextIP = rout.previousIP;

                    Socket socket1 = new Socket(nextIP, 8090);
                    DataOutputStream out = new DataOutputStream(socket1.getOutputStream());

                    out.writeUTF("FLOW");
                    out.writeUTF(message);

                    socket1.close();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
