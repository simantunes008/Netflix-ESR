import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class oNode {

    private static DatagramSocket forOverlay_s;
    private static InetAddress ipServer;


    public static void overlay_infos(){
        
        try{
            String mensagem = "HELLO, server! Can you please send me my neighbours?";

            byte[] buffer = mensagem.getBytes();

            // Criar o pacote
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ipServer, 2020);

            forOverlay_s.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    public static void main(String[] args) {
        if (args.length == 3 && args[1].equals("-b")) {
            String name = args[2];
            Server s = new Server(Server.ServerMode.BOOTSTRAPPER, name);
            s.start();
            overlay_infos();

        } else if (args.length == 3 && args[2].equals("-c")) {
            //Client client = new Client();

        } else if (args.length == 3 && args[2].equals("-s")) {
            Server s = new Server(Server.ServerMode.SIMPLE, null); 
            s.start();
        }
    }


    


}
