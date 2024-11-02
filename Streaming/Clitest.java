import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Clitest {
    static int RTP_RCV_PORT = 25000;

    public static void main(String[] args) {
        System.out.println("Debug - Inicializando cliente");
        try {
            DatagramSocket socket = new DatagramSocket(RTP_RCV_PORT);
            byte[] buffer = new byte[15000];
            System.out.println("Cliente aguardando pacotes na porta " + RTP_RCV_PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                System.out.println("Pacote recebido: " + packet.getLength() + " bytes de " + packet.getAddress());
            }
        } catch (Exception e) {
            System.out.println("Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
