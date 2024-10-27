package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class oNode {
    public static void main(String[] args) throws IOException {
        if (args[0].equals("-b")) {
            new Thread(new Bootstrapper(args[1])).start();
        }

        if (args[0].equals("-sc")) {
            // Inicia o servidor
            new Thread(new Server()).start();

            // Liga-se ao bootstrapper para pedir os vizinhos
            Socket socket = new Socket(args[1], 8080);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("JOIN");

            List<String> neighbours = new ArrayList<>();
            int size = in.readInt();

            for (int i = 0; i < size; i++) {
                String s = in.readUTF();
                System.out.println(s);
                neighbours.add(s);
            }

            out.writeUTF("CLOSE");
            socket.close();
        }
    }
}