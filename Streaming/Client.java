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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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

    public Client() {

        /*duas vertentes de um cliente:
            1. Se ele for reproduzir o vídeo,
         */

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

        try {
            this.RTPsocket = new DatagramSocket(RTP_RCV_PORT);
            System.out.println("Client Here!! I'm connected on port: " + RTP_RCV_PORT);
            //this.RTPsocket.setSoTimeout(5000);
            // Adicionado
            this.cTimer.start();
        } catch (SocketException var2) {
            System.out.println("Cliente: erro no socket: " + var2.getMessage());
        }

    }

    public static void main(String[] var0) throws Exception {
        new Client();
    }

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

    class tearButtonListener implements ActionListener {
        tearButtonListener() {
        }

        public void actionPerformed(ActionEvent var1) {
            System.out.println("Teardown Button pressed !");
            Client.this.cTimer.stop();


            //Adicionado
            Client.this.RTPsocket.close(); // Fecha o socket


            System.exit(0);
        }
    }

    class playButtonListener implements ActionListener {
        playButtonListener() {
        }

        public void actionPerformed(ActionEvent var1) {
            System.out.println("Play Button pressed !");
            Client.this.cTimer.start();
        }
    }
}
