package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerTHD implements Runnable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private Routs routs;

    ServerTHD(Socket socket, Routs routs) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.routs = routs;
    }

    @Override
    public void run() {
        try {
            String message = in.readUTF();

            if (message.equals("HELLO")) {

                String previousNode = this.socket.getInetAddress().getHostAddress();
                int jumps = in.readInt() + 1;
                long delay = System.currentTimeMillis() - this.in.readLong();

                Rout rout = new Rout(previousNode, jumps, delay);
                System.out.println(rout.toString());
            }

            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
