/**
 * Servidor responsável pelo papel de Bootstrapper
 * Cada Node liga-se a ele para receber a sua lista de Nodes vizinhos
 * Essa lista é conseguida a partir de um ficheiro de configuração
 * */

package oNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Bootstrapper implements Runnable {
    private final String configFile;

    public Bootstrapper(String configure) {
        this.configFile = configure;
    }

    @Override
    public void run() {
        Map<String, List<String>> overlay = new HashMap<>();
        File configFile = new File(this.configFile);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            int nodes = Integer.parseInt(reader.readLine().trim());

            for (int i = 0; i < nodes; i++) {
                String line = reader.readLine().trim();
                String[] parts = line.split(" ");
                String nodeIP = parts[0];
                List<String> neighbors = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));
                overlay.put(nodeIP, neighbors);
            }

            System.out.println("Configuração carregada!");
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Bootstrapper à escuta na porta 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                new Thread(new BootstrapperTHD(clientSocket, overlay.get(clientIP))).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
