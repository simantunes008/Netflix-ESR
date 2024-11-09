package Streaming;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

import oNode.Flows;
import oNode.Flow;


public class Server extends JFrame {

    //GUI:
    JLabel label;

    //RTP variables:
    DatagramPacket senddp; //UDP packet containing the video frames (to send)A
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet
    int RTP_dest_port = 25000; //destination port for RTP packets

    static String VideoFileName; //video file to request to the server

    VideoStream video; //VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; //length of the video in frames

    private static Flows flows;

    //Construtor teste para apenas um cliente
    public Server() {
        // Configuração da GUI (opcional)
        super("Servidor");
        label = new JLabel("Aguardando conexão do cliente...", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);
        setSize(300, 100);
        setVisible(true);

        try {
            video = new VideoStream(VideoFileName);
            System.out.println("Servidor: Iniciando streaming do vídeo " + VideoFileName);
        } catch (Exception e) {
            System.out.println("Erro ao inicializar o vídeo: " + e.getMessage());
            System.exit(0);
        }

        try {
            System.out.println("Servidor: Socket criado na porta " + RTPsocket.getLocalPort());

            String clientIP = "192.168.1.100";
            InetAddress ClientIPAddr = InetAddress.getByName(clientIP);
            ClientHandler clientHandler = new ClientHandler(ClientIPAddr);
            clientHandler.start();
        } catch (Exception e) {
            System.out.println("Erro ao processar cliente: " + e.getMessage());
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    //Construtor a ser usado
    public Server(Flows flows){
        this.flows = flows;

        super("Servidor");
        label = new JLabel("Send frame #        ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);

        try {
            RTPsocket = new DatagramSocket();
            for (Flow f : flows.flows) {
                for (String s : f.targets) {
                    InetAddress ClientIP = InetAddress.getByName(s);
                    new ClientHandler(ClientIP).start();
                }
            }
        } catch (SocketException e) {
            System.out.println("Servidor: erro no socket: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Servidor: erro no video: " + e.getMessage());
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }


    //main
    public static void main(String argv[]) throws Exception {
        if (argv.length >= 1 ) {
            VideoFileName = argv[0];
        } else  {
            VideoFileName = "movie.Mjpeg";
        }

        File f = new File(VideoFileName);
        if (f.exists()) {
            Server s = new Server(flows);
            //show GUI: (opcional!)
            //s.pack();
            //s.setVisible(true);
        } else
            System.out.println("\nINFO EXTRA: Não existe o seguinte vídeo " + VideoFileName);
    }


    class ClientHandler extends Thread implements ActionListener{
        private InetAddress clientIP;
        private Timer timer;
        private VideoStream video;
        private int imagenb = 0;
        private byte[] sBuf = new byte[15000];

        public ClientHandler(InetAddress clientIP) {
            this.clientIP = clientIP;
            try {
                this.video = new VideoStream(VideoFileName);
                this.timer = new Timer(FRAME_PERIOD, this);
                this.timer.setInitialDelay(0);
                this.timer.setCoalesce(true);
            } catch (Exception e) {
                System.out.println("Erro ao iniciar o vídeo para " + clientIP + ": " + e.getMessage());
            }
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("actionPerformed: Preparando para enviar frame #" + imagenb);
            // Se o número da imagem atual é menor que o comprimento do vídeo
            if (imagenb < VIDEO_LENGTH) {
                // Atualiza o número da imagem atual
                imagenb++;

                try {
                    // Primeiro frame + seu tamanho
                    int image_length = video.getnextframe(sBuf);

                    RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb * FRAME_PERIOD, sBuf, image_length);
                    int packet_length = rtp_packet.getlength();

                    // Get Bitstream do pacote e armazena em um array de bytes
                    byte[] packet_bits = new byte[packet_length];
                    rtp_packet.getpacket(packet_bits);

                    // Cria e envia o pacote como DatagramPacket pelo socket UDP
                    senddp = new DatagramPacket(packet_bits, packet_length, clientIP, RTP_dest_port);
                    RTPsocket.send(senddp);
                    System.out.println("Frame #" + imagenb + " enviado para " + clientIP);

                    // Atualiza a GUI (opcional)
                    // label.setText("Send frame #" + imagenb);
                } catch (Exception ex) {
                    System.out.println("Erro ao enviar frame para " + clientIP + ": " + ex.getMessage());
                    ex.printStackTrace();
                    timer.stop();
                }
            } else {
                timer.stop(); // Para o temporizador se o vídeo acabou
            }
        }

        // Inicia o temporizador quando a thread é iniciada
        @Override
        public void run() {
            System.out.println("Iniciando envio de vídeo para " + clientIP);
            timer.start();
        }
    }



}//end of Class Servidor