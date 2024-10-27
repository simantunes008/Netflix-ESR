package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerTHD implements Runnable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    ServerTHD(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            String message = in.readUTF();
            System.out.println("Recebido: " + message);
            out.writeUTF("Echo: " + message);
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
