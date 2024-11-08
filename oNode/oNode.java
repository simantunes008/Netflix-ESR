package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class oNode {
    public static void main(String[] args) throws IOException {

        List<String> neighbours = new ArrayList<>();
        Routs routs = new Routs();
        Flows flows = new Flows();

        if (args.length == 2 && args[0].equals("-b")) {
            // Node em modo Bootstrapper
            new Thread(new Bootstrapper(args[1])).start();

        } else if (args.length == 3 && args[0].equals("-s")) {
            // Inicia o servidor para enviar pacotes
            new Thread(new Server(neighbours, routs, flows)).start();

            // Servidor UDP para enviar a lista de points of presence
            new Thread(new ServerUDP(args[2])).start();

            // Liga-se ao bootstrapper para pedir os vizinhos
            Socket socket = new Socket(args[1], 8080);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("JOIN");

            int size = in.readInt();

            for (int i = 0; i < size; i++) {
                neighbours.add(in.readUTF());
            }

            out.writeUTF("CLOSE");
            socket.close();

            // Executa o monitor da rede Overlay
            new Thread(new Monitor(neighbours)).start();

        } else if (args.length == 2 && args[0].equals("-n")) {
            // Inicia o servidor para enviar pacotes
            new Thread(new Server(neighbours, routs, flows)).start();

            // Liga-se ao bootstrapper para pedir os vizinhos
            Socket socket = new Socket(args[1], 8080);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("JOIN");

            int size = in.readInt();

            for (int i = 0; i < size; i++) {
                neighbours.add(in.readUTF());
            }

            out.writeUTF("CLOSE");
            socket.close();

        } else if (args.length == 2 && args[0].equals("-pop")) {
            // Inicia o servidor para enviar pacotes
            new Thread(new Server(neighbours, routs, flows)).start();

            // Servidor UDP para receber pacotes dos clientes
            new Thread(new popWorker(routs, flows)).start();

            // Liga-se ao bootstrapper para pedir os vizinhos
            Socket socket = new Socket(args[1], 8080);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("JOIN");

            int size = in.readInt();

            for (int i = 0; i < size; i++) {
                neighbours.add(in.readUTF());
            }

            out.writeUTF("CLOSE");
            socket.close();

        } else {
            System.out.println("Opções de utilização inválidas");
        }
    }
}