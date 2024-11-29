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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Forwarder {
    private Flows flows;
    private ExecutorService executorService;

    // ### GUI ###
    JLabel label;

    DatagramPacket receivePacket;
    DatagramSocket Socket_receiver;
    DatagramPacket sendPacket;

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

    private Map<Integer, DatagramSocket> socketMap = new HashMap<>();
    

    public Forwarder(Flows flows){

        this.flows = flows;
        this.executorService = Executors.newFixedThreadPool(10);
        receiveTimer = new Timer(20, new clientTimerListener());
        receiveTimer.setInitialDelay(0);
        this.receiveTimer.setCoalesce(true);
        this.receiveBuf = new byte[15000];
        this.sendBuf = new byte[15000];

        try{
            Socket_receiver = new DatagramSocket(RTP_port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

       receiveTimer.start();
    }

    private DatagramSocket getSocket(int flowId) throws SocketException {
        synchronized (socketMap) {
            return socketMap.computeIfAbsent(flowId, id -> {
                try {
                    return new DatagramSocket();
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    class clientTimerListener implements ActionListener {
        clientTimerListener() {
        }

        public void actionPerformed(ActionEvent var1) {
                receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                if (imagenb < VIDEO_LENGTH){
                    try{
                        Socket_receiver.receive(receivePacket);

                        int id = ((receiveBuf[12] & 0xFF) << 24) | ((receiveBuf[13] & 0xFF) << 16) |
                                ((receiveBuf[14] & 0xFF) << 8) | (receiveBuf[15] & 0xFF);

                        Flow f = flows.flows.get(String.valueOf(id));
                            
                        for (String s : f.targets) {
                            InetAddress ip = InetAddress.getByName(s);

                            executorService.submit(() -> {
                                try {
                                    DatagramSocket Socket_sender = getSocket(id);
                                    DatagramPacket packetCopy = new DatagramPacket(receiveBuf, receiveBuf.length);
                                    
                                    packetCopy.setAddress(ip);
                                    packetCopy.setPort(RTP_port);
                                    Socket_sender.send(packetCopy);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
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