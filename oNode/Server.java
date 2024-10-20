public class Server implements Runnable{

    private final int SERVER_PORT = 2020;
    private List<InetAdress> my_neighbors;
    private int nodes;
    private Map<String, Flow> flows;
    private Map<String, Route> routes_table;

    public void run() {
        try {
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
                socket.receive(receivePacket);
                
                new Thread(new ClientHandler(socket, receivePacket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}