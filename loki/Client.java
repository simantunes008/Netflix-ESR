import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int port = 8080;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(args[0], port);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        Scanner sc = new Scanner(System.in);
        System.out.print("Mensagem: ");
        String message = sc.nextLine();
        out.writeUTF(message);

        String response = in.readUTF();
        System.out.println(response);

        socket.close();
    }
}
