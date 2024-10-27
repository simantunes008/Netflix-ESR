package oNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bootstrapper implements Runnable {
    private final String configure;

    public Bootstrapper(String configure) {
        this.configure = configure;
    }

    @Override
    public void run() {
        Map<String, List<String>> overlay = new HashMap<>();
        File configFile = new File(configure);

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            int numNodes = Integer.parseInt(reader.readLine().trim());

            for (int i = 0; i < numNodes; i++) {
                String line = reader.readLine().trim();
                String[] parts = line.split(" ");
                String nodeIP = parts[0].substring(1); // Remove a barra inicial '/'

                List<String> neighbors = new ArrayList<>();
                for (int j = 1; j < parts.length; j++) {
                    neighbors.add(parts[j]);
                }

                overlay.put(nodeIP, neighbors);
            }

            System.out.println("Configuração carregada!");
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Bootstrapper à escuta na porta 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String ip = clientSocket.getInetAddress().getHostAddress();
                new Thread(new BootstrapperTHD(clientSocket, overlay.get(ip))).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
