package Streaming;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import oNode.Flows;
import oNode.Flow;


public class Server_str extends JFrame {

    //GUI:
    JLabel label;

    //RTP variables:
    DatagramPacket senddp; //UDP packet containing the video frames (to send)A
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet
    int RTP_dest_port = 25000; //destination port for RTP packets

    static String VideoFileName = "Streaming/movie.Mjpeg"; //video file to request to the server

    VideoStream video; //VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; //length of the video in frames

    private static Flows flows;

    boolean isStreaming = false;

    //Construtor a ser usado
    public Server_str(Flows flows) {
        super("Servidor");

        System.out.println("Sou o servidor de Streaming e inicializei. \n");

        this.flows = flows;

        label = new JLabel("Send frame #        ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);

        try {
            // Criação do socket de envio
            RTPsocket = new DatagramSocket();

            // Criação do socket de escuta
            DatagramSocket listeningSocket = new DatagramSocket(25000); // Porta do servidor para conexões
            System.out.println("Servidor: aguardando conexões na porta 25000");

            // Thread para escutar conexões dos clientes
            new Thread(() -> {
                while (true) {
                    try {
                        byte[] buffer = new byte[1024];
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                        // Recebe mensagem de conexão do cliente
                        listeningSocket.receive(request);
                        String message = new String(request.getData(), 0, request.getLength());
                        System.out.println("Servidor recebeu: " + message);

                        if (message.equals("START") && !isStreaming) {
                            // Está a fazer stream
                            isStreaming = true;
                            // Processamento dos fluxos
                            // TODO: Implementar vários vídeos
                            for (Map.Entry<String, Flow> entry : flows.flows.entrySet()) {
                                Flow f = entry.getValue();
                                for (String s : f.targets) {
                                    InetAddress ClientIP = InetAddress.getByName(s);
                                    new ClientHandler(ClientIP).start();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Erro ao escutar conexões: " + e.getMessage());
                    }
                }
            }).start();


            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
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
            Server_str s = new Server_str(flows);
            //show GUI: (opcional!)
            //s.pack();
            //s.setVisible(true);
        } else
            System.out.println("\nINFO EXTRA: Não existe o seguinte vídeo " + VideoFileName);
    }


    class ClientHandler extends Thread implements ActionListener{
        private InetAddress clientIP;
        private Server_str server;
        private Timer timer;
        private VideoStream video;
        private int imagenb = 0;
        private byte[] sBuf = new byte[15000];

        public ClientHandler(InetAddress clientIP) {
            this.clientIP = clientIP;
            this.server = Server_str.this;
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
                server.isStreaming = false;
                timer.stop(); // Para o temporizador se o vídeo acabou
            }
        }

        // Inicia o temporizador quando a thread é iniciada
        @Override
        public void run() {
            System.out.println("Iniciando envio de vídeo " + VideoFileName + " para " + clientIP);
            timer.start();
        }
    }



}//end of Class Servidor