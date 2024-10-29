package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.Socket;
import java.util.List;

public class Monitor implements Runnable {
    private List<String> neighbors;

    public Monitor(List<String> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (String neighbor : neighbors) {
                    try {
                        Socket socket = new Socket(neighbor, 8090);

                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        out.writeUTF("HELLO");
                        String response = in.readUTF();
                        System.out.println(response);

                        socket.close();
                    } catch (Exception e) {
                        System.out.println("Vizinho " + neighbor + " não está conectado");
                    }
                }

                // Envia mensagens a cada 1 segundo
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
