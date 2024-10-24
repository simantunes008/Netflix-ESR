import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server {

    public enum ServerMode {
        BOOTSTRAPPER , SIMPLE
    }
    private String name_node_recieved;
    private ServerMode mode;

    private final int SERVER_PORT = 2020;
    private int number_of_nodes = 12;
    private List<InetAddress> up_nodes = new ArrayList<>();
    private List<InetAddress> overlayNodes; 
    private Map<String, Flow> flows;
    private Map<String, Route> routes_table;

    public Server(ServerMode mode, String name_node_recieved) {
        this.mode = mode;
        this.name_node_recieved = name_node_recieved; // Armazena o nome
    }


    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case BOOTSTRAPPER:
                        parserConFile(name_node_recieved);
                        break;
                    case SIMPLE:
                        //runSimpleServer();
                        break;
                }
            }
        });
        thread.start();
    }

    public void overlay_construction (String name){
        parserConFile(name);
    }


    public void parserConFile(String name){

        List<InetAddress> neighbors = new ArrayList<>();

        String path = "../../ConfigFiles/ConfigFile " + name;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            if ((line = reader.readLine()) != null) {
                InetAddress ip_n = InetAddress.getByName(line.trim());
                this.up_nodes.add(ip_n);
            }

            boolean emptyLineFound = false;
            while (!emptyLineFound && (line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    emptyLineFound = true;
                } else {
                    InetAddress ip_n = InetAddress.getByName(line.trim());
                    neighbors.add(ip_n);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
