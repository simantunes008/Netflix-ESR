public class ClientHandler implements Runnable {
    private DatagramSocket socket;
    private DatagramPacket packet;

    public ClientHandler(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
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