//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Streaming;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Client {
    JFrame f = new JFrame("Cliente de Testes");
    JButton setupButton = new JButton("Setup");
    JButton playButton = new JButton("Play");
    JButton pauseButton = new JButton("Pause");
    JButton tearButton = new JButton("Teardown");
    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel iconLabel = new JLabel();
    ImageIcon icon;
    DatagramPacket rcvdp;
    DatagramSocket RTPsocket;
    static int RTP_RCV_PORT = 25000;
    Timer cTimer;
    byte[] cBuf;
    String server_ip;
    String pop;
    DatagramSocket socket;
    String videoID;

    public Client(String servidor, String pop, DatagramSocket socket, String videoID) {
        this.f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
                System.exit(0);
            }
        });
        this.buttonPanel.setLayout(new GridLayout(1, 0));
        this.buttonPanel.add(this.setupButton);
        this.buttonPanel.add(this.playButton);
        this.buttonPanel.add(this.pauseButton);
        this.buttonPanel.add(this.tearButton);
        this.playButton.addActionListener(new playButtonListener());
        this.tearButton.addActionListener(new tearButtonListener());
        this.iconLabel.setIcon((Icon)null);
        this.mainPanel.setLayout((LayoutManager)null);
        this.mainPanel.add(this.iconLabel);
        this.mainPanel.add(this.buttonPanel);
        this.iconLabel.setBounds(0, 0, 380, 280);
        this.buttonPanel.setBounds(0, 280, 380, 50);
        this.f.getContentPane().add(this.mainPanel, "Center");
        this.f.setSize(new Dimension(390, 370));
        this.f.setVisible(true);
        this.cTimer = new Timer(20, new clientTimerListener());
        this.cTimer.setInitialDelay(0);
        this.cTimer.setCoalesce(true);
        this.cBuf = new byte[15000];

        this.server_ip = servidor;
        this.pop = pop;
        this.socket = socket;
        this.videoID = videoID;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sendEndMessage();
        }));

        try {

            // Envia mensagem inicial para o servidor
            DatagramSocket setupSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(server_ip);
            DatagramPacket startPacket = new DatagramPacket(videoID.getBytes(), videoID.length(), serverAddress, RTP_RCV_PORT);
            setupSocket.send(startPacket);
            setupSocket.close();

            System.out.println("Mensagem enviada ao servidor ");


            this.RTPsocket = new DatagramSocket(RTP_RCV_PORT);
            System.out.println("Cliente de streaming, e estou conectado na porta " + RTP_RCV_PORT);
            //this.RTPsocket.setSoTimeout(5000);
            // Adicionado
            this.cTimer.start();
        } catch (SocketException var2) {
            System.out.println("Cliente Streaming: erro no socket: " + var2.getMessage());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /*public static void main(String[] var0) throws Exception {
        new Client();
    }*/

    class clientTimerListener implements ActionListener {
        clientTimerListener() {
        }

        public void actionPerformed(ActionEvent var1) {
            Client.this.rcvdp = new DatagramPacket(Client.this.cBuf, Client.this.cBuf.length);

            try {
                Client.this.RTPsocket.receive(Client.this.rcvdp);
                RTPpacket var2 = new RTPpacket(Client.this.rcvdp.getData(), Client.this.rcvdp.getLength());
                PrintStream var10000 = System.out;
                int var10001 = var2.getsequencenumber();
                var10000.println("Got RTP packet with SeqNum # " + var10001 + " TimeStamp " + var2.gettimestamp() + " ms, of type " + var2.getpayloadtype());
                var2.printheader();
                int var3 = var2.getpayload_length();
                byte[] var4 = new byte[var3];
                var2.getpayload(var4);
                Toolkit var5 = Toolkit.getDefaultToolkit();
                Image var6 = var5.createImage(var4, 0, var3);
                Client.this.icon = new ImageIcon(var6);
                Client.this.iconLabel.setIcon(Client.this.icon);
            } catch (InterruptedIOException var7) {
                System.out.println("Nothing to read");
            } catch (IOException var8) {
                System.out.println("Exception caught: " + var8);
            }

        }
    }

    private void sendEndMessage() {
        try {
            byte[] endMessage = ("END," + this.server_ip + "," + this.videoID).getBytes();
            DatagramPacket endPacket = new DatagramPacket(endMessage, endMessage.length, InetAddress.getByName(this.pop), 8070);
            this.socket.send(endPacket);
            System.out.println("Mensagem 'END' enviada para " + this.pop);
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao enviar mensagem 'END': " + e.getMessage());
        }
    }

    class tearButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent var1) {
            System.out.println("Teardown Button pressed !");
            cTimer.stop();
            System.exit(0);
        }
    }

    class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){

            System.out.println("Play Button pressed !");
            cTimer.start();
        }
    }

    class pauseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            System.out.println("Pause Button pressed !");
            cTimer.stop();
        }
    }
}