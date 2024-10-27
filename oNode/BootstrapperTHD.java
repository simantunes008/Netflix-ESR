package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class BootstrapperTHD implements Runnable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final List<String> neighbors;

    public BootstrapperTHD(Socket socket, List<String> neighbors) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.neighbors = neighbors;
    }

    @Override
    public void run() {
        try {
            String message = in.readUTF();

            if (message.equals("JOIN")) {
                out.writeInt(neighbors.size());

                for (String neighbor : neighbors) {
                    out.writeUTF(neighbor);
                }
            }

            if (message.equals("CLOSE")) {
                socket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
