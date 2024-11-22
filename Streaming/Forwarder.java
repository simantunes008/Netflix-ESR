package Streaming;

import oNode.Flow;
import oNode.Flows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;

public class Forwarder {
    private Flows flows;

    // ### GUI ###
    JLabel label;

    DatagramPacket receivePacket;
    DatagramSocket Socket_receiver;
    DatagramPacket sendPacket;
    DatagramSocket Socket_sender;

    int RTP_port = 25000;

    int imagenb = 0;
    VideoStream video;
    static int MJPEG_TYPE = 26;
    static int FRAME_PERIOD = 40;
    static int VIDEO_LENGTH = 500;

    Timer receiveTimer;
    byte[] receiveBuf;
    Timer sendTimer;
    byte[] sendBuf;


    public Forwarder(Flows flows){

        this.flows = flows;
        receiveTimer = new Timer(20, new clientTimerListener());
        receiveTimer.setInitialDelay(0);
        this.receiveTimer.setCoalesce(true);
        this.receiveBuf = new byte[15000];
        this.sendBuf = new byte[15000];

        try{
            Socket_receiver = new DatagramSocket(RTP_port);
            Socket_sender = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

       receiveTimer.start();
    }


    class clientTimerListener implements ActionListener {
        clientTimerListener() {
        }

        public void actionPerformed(ActionEvent var1) {
                receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                if (imagenb < VIDEO_LENGTH){
                    try{
                        Socket_receiver.receive(receivePacket);
                        System.out.println("Pacote recebido: SeqNum # " + imagenb + " de " + receivePacket.getAddress() + ":" + receivePacket.getPort());

                        for (Map.Entry<String, Flow> entry : flows.flows.entrySet()) {
                            Flow f = entry.getValue();
                            for (String s : f.targets) {
                                InetAddress ip = InetAddress.getByName(s);

                                Thread t = new Thread(() -> {
                                    DatagramPacket packetCopy = new DatagramPacket(receiveBuf, receiveBuf.length);

                                    synchronized(Socket_sender) {
                                        packetCopy.setAddress(ip);
                                        packetCopy.setPort(RTP_port);
                                        System.out.println("Encaminhando pacote para " + ip + ":" + RTP_port);

                                        try {
                                            Socket_sender.send(packetCopy);
                                            System.out.println("Pacote encaminhado para " + ip);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                                t.start();
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    receiveTimer.restart();
                    imagenb=0;
                }
        }
    }
}
