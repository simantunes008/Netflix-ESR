package oNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerTHD implements Runnable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private List<String> neighbors;
    private Routs routs;
    private Flows flows;

    ServerTHD(Socket socket, List<String> neighbors, Routs routs, Flows flows) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.neighbors = neighbors;
        this.routs = routs;
        this.flows = flows;
    }

    private void forward(String neighborIP, String serverIP, int jumps, long delay) {
        try {
            Socket socket = new Socket(neighborIP, 8090);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("HELLO");
            out.writeUTF(serverIP);
            out.writeInt(jumps);
            out.writeLong(System.currentTimeMillis());
            out.writeLong(delay);
            socket.close();
        } catch (IOException e) {
            System.out.println("Vizinho " + neighborIP + " não está conectado");
        }
    }

    private void printInfo() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("---------TABELA DE ROTAS----------");
        System.out.println(routs.toString());
        System.out.println("---------TABELA DE FLUXOS---------");
        System.out.println(flows.toString());
    }

    @Override
    public void run() {
        try {
            String message = in.readUTF();

            if (message.equals("HELLO")) {
                List<String> temp = new ArrayList<>(neighbors);
                temp.removeIf(n -> n.equals(this.socket.getInetAddress().getHostAddress()));

                String previousNode = this.socket.getInetAddress().getHostAddress();
                String serverIP = in.readUTF();
                int jumps = in.readInt() + 1;
                long delay = System.currentTimeMillis() - this.in.readLong() + this.in.readLong();

                Rout rout = new Rout(previousNode, jumps, delay);
                boolean hasChanged = routs.insertRout(serverIP, rout);

                if (hasChanged) {
                    for (String neighborIP : temp) {
                        forward(neighborIP, serverIP, jumps, delay);
                    }
                }

                printInfo();
            } else if (message.equals("FLOW")) {
                String content = in.readUTF();
                String targetServer = in.readUTF();

                if (targetServer.equals(this.socket.getLocalAddress().getHostAddress())) {
                    flows.addFlow(content, "", this.socket.getInetAddress().getHostAddress(), "");
                } else {
                    String nextIP = routs.routs.get(targetServer).previousIP;

                    this.flows.addFlow(content, targetServer, this.socket.getInetAddress().getHostAddress(), nextIP);

                    Socket socket = new Socket(nextIP, 8090);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    out.writeUTF("FLOW");
                    out.writeUTF(content);
                    out.writeUTF(targetServer);

                    socket.close();
                }

                printInfo();
            } else if (message.equals("END")) {
                String content = in.readUTF();
                String targetServer = in.readUTF();

                Flow f = this.flows.flows.get(content);
                f.targets.remove(this.socket.getInetAddress().getHostAddress());
                if (f.targets.isEmpty() && !targetServer.equals(this.socket.getLocalAddress().getHostAddress())) {
                    String nextIP = routs.routs.get(targetServer).previousIP;

                    Socket socket = new Socket(nextIP, 8090);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    out.writeUTF("END");
                    out.writeUTF(content);
                    out.writeUTF(targetServer);

                    socket.close();
                }
            }

            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
