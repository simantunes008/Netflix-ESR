package oNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server implements Runnable {
    private List<String> neighbors;
    private Routs routs;

    public Server(List<String> neighbors, Routs routs) {
        this.routs = routs;
        this.neighbors = neighbors;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8090);
            System.out.println("Servidor à escuta na porta 8090");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServerTHD(clientSocket, neighbors, routs)).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
