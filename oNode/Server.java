package oNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private Routs routs;

    public Server(Routs routs) {
        this.routs = routs;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8090);
            System.out.println("Servidor à escuta na porta 8090");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServerTHD(clientSocket, routs)).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
