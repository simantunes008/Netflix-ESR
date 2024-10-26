import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket serverSocket;
    private static final int port = 8080;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor à escuta na porta 8080");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado");

        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        String message = in.readUTF();
        System.out.println("Recebido: " + message);

        out.writeUTF("Mensagem recebida pelo servidor");
        System.out.println("Encerrando o servidor...");

        clientSocket.close();
        serverSocket.close();
    }
}
