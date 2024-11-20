package oNode;

import java.io.IOException;
import java.net.*;

import Streaming.Client;

public class oClient {
    private static final int TIMEOUT = 1000;
    private static final int PING_ATTEMPTS = 3;

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);

            byte[] buf = "HELLO".getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(args[0]), 8070);
            socket.send(packet);

            byte[] responseBuf = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuf, responseBuf.length);
            socket.receive(responsePacket);

            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] pointsOfPresence = response.split(",");

            String bestNode = "";
            long bestLatency = Long.MAX_VALUE;

            for (String pop : pointsOfPresence) {
                InetAddress popAddress = InetAddress.getByName(pop);
                long totalLatency = 0;
                int successfulPings = 0;

                for (int i = 0; i < PING_ATTEMPTS; i++) {
                    try {
                        long startTime = System.currentTimeMillis();

                        byte[] pingMessage = "PING".getBytes();
                        DatagramPacket pingPacket = new DatagramPacket(pingMessage, pingMessage.length, popAddress, 8070);
                        socket.send(pingPacket);

                        DatagramPacket responsePingPacket = new DatagramPacket(new byte[1024], 1024);
                        socket.receive(responsePingPacket);

                        totalLatency += System.currentTimeMillis() - startTime;
                        successfulPings++;
                    } catch (SocketTimeoutException e) {
                        System.out.println("Ping para " + pop + " #" + (i + 1) + " falhou (timeout)");
                    }
                }

                if (successfulPings > 0) {
                    long averageLatency = totalLatency / successfulPings;
                    System.out.println("Ponto de presença: " + pop);
                    System.out.println("Delay médio: " + averageLatency + " ms");
                    System.out.println("----------------------------------");
                    if (averageLatency < bestLatency) {
                        bestLatency = averageLatency;
                        bestNode = pop;
                    }

                } else {
                    System.out.println("Nenhuma resposta do PoP " + pop + " após " + PING_ATTEMPTS + " tentativas");
                }
            }

            byte[] pingMessage = ("FLOW," + args[0]).getBytes();
            DatagramPacket pingPacket = new DatagramPacket(pingMessage, pingMessage.length, InetAddress.getByName(bestNode), 8070);
            socket.send(pingPacket);

            // Inicia o cliente de streaming
            System.out.println("#NEXT# : CLIENTE DE STREAMING");
            new Streaming.Client(args[0], bestNode, socket);

        } else {
            System.out.println("Usage:\n" +
                    " Client                   java oNode.oClient [SERVER_IP]");
        }
    }
}
