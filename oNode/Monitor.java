package oNode;

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
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("HELLO");
                        out.writeUTF(socket.getLocalAddress().getHostAddress());
                        out.writeInt(0);
                        out.writeLong(System.currentTimeMillis());
                        out.writeLong(0);
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
