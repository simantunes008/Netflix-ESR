import java.util.ArrayList;
import java.util.List;

public class oNode {

    public static void main(String[] args) {
        if (args.length == 3 && args[1].equals("-b")) {
            String name = args[2];
            Server s = new Server(Server.ServerMode.BOOTSTRAPPER, name);
            s.start();

        } else if (args.length == 3 && args[2].equals("-c")) {
            Client client = new Client();

        } else if (args.length == 3 && args[2].equals("-s")) {
            Server s = new Server(Server.ServerMode.SIMPLE, null); 
            s.start();
        }
    }
}
