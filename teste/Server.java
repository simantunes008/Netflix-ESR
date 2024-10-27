package teste;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Servidor à escuta na porta 8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado");

            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            String message = in.readUTF();
            System.out.println("Recebido: " + message);
            out.writeUTF("Echo: " + message);

            clientSocket.close();
        }
    }
}
