package oNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class ServerUDP implements Runnable {
    private final String configFile;

    public ServerUDP(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public void run() {
        List<String> pointsOfPresence = new ArrayList<>();
        File configFile = new File(this.configFile);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line = reader.readLine().trim();
            String[] parts = line.split(" ");
            Collections.addAll(pointsOfPresence, parts);

            DatagramSocket socket = new DatagramSocket(8070);
            System.out.println("Configuração carregada!");
            System.out.println("Servidor UDP à escuta na porta 8070");

            // TODO: Implementar threads

            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                if (message.equals("HELLO")) {
                    String pointsOfPresenceList = String.join(",", pointsOfPresence);
                    byte[] response = pointsOfPresenceList.getBytes();

                    packet = new DatagramPacket(response, response.length, clientAddress, clientPort);
                    socket.send(packet);

                }

                socket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
