import java.net.DatagramPacket;
import java.net.Socket;

public class Client implements Runnable {
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Mensagem recebida: " + message);

            String responseMessage = "Mensagem recebida: " + message;
            byte[] responseData = responseMessage.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                    responseData,
                    responseData.length,
                    packet.getAddress(),
                    packet.getPort()
            );

            socket.send(responsePacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
