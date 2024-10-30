package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerTHD implements Runnable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private List<String> neighbors;
    private Routs routs;

    ServerTHD(Socket socket, List<String> neighbors, Routs routs) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.neighbors = neighbors;
        this.routs = routs;
    }

    public void forward(String neighborIP, String serverIP, int jumps, long delay) {
        try {
            Socket socket = new Socket(neighborIP, 8090);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("HELLO");
            out.writeUTF(serverIP);
            out.writeInt(jumps);
            out.writeLong(System.currentTimeMillis());
            out.writeLong(delay);
            socket.close();
        } catch (IOException e) {
            System.out.println("Vizinho " + neighborIP + " não está conectado");
        }
    }

    @Override
    public void run() {
        try {
            String message = in.readUTF();

            if (message.equals("HELLO")) {
                List<String> temp = new ArrayList<>(neighbors);
                temp.removeIf(n -> n.equals(this.socket.getInetAddress().getHostAddress()));

                String previousNode = this.socket.getInetAddress().getHostAddress();
                String serverIP = in.readUTF();
                int jumps = in.readInt() + 1;
                long delay = System.currentTimeMillis() - this.in.readLong() + this.in.readLong();

                Rout rout = new Rout(previousNode, jumps, delay);
                boolean massa = routs.insertRout(serverIP, rout);

                if (massa) {
                    for (String neighborIP : temp) {
                        forward(neighborIP, serverIP, jumps, delay);
                    }
                }

                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println(routs.toString());
            }

            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
