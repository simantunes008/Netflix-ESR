/* ------------------
   Servidor
   usage: java Servidor [Video file]
   adaptado dos originais pela equipa docente de ESR (nenhumas garantias)
   colocar primeiro o cliente a correr, porque este dispara logo
   ---------------------- */
package Streaming;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;


public class Server extends JFrame implements ActionListener {

    //GUI:
    //----------------
    JLabel label;

    //RTP variables:
    //----------------
    DatagramPacket senddp; //UDP packet containing the video frames (to send)A
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet
    int RTP_dest_port = 25000; //destination port for RTP packets
    InetAddress ClientIPAddr; //Client IP address

    static String VideoFileName; //video file to request to the server

    //Video constants:
    //------------------
    int imagenb = 0; //image nb of the image currently transmitted
    VideoStream video; //VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; //length of the video in frames

    Timer sTimer; //timer used to send the images at the video frame rate
    byte[] sBuf; //buffer used to store the images to send to the client

    //--------------------------
    //Constructor
    //--------------------------
    public Server() {
        //init Frame
        super("Servidor");

        // init para a parte do servidor
        sTimer = new Timer(FRAME_PERIOD, this); //init Timer para servidor
        sTimer.setInitialDelay(0);
        sTimer.setCoalesce(true);
        sBuf = new byte[15000]; //allocate memory for the sending buffer

        try {
            RTPsocket = new DatagramSocket(); //iniciar o socket RTP
            ClientIPAddr = InetAddress.getByName("127.0.0.1");
            System.out.println("Servidor: socket " + ClientIPAddr);
            video = new VideoStream(VideoFileName); //iniciar o objeto VideoStream
            System.out.println("Servidor: vai enviar video da file " + VideoFileName);

            System.out.println("Servidor: Iniciando temporizador...");
            sTimer.start();  // Iniciar o temporizador
            System.out.println("Servidor: Temporizador iniciado");



        } catch (SocketException e) {
            System.out.println("Servidor: erro no socket: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Servidor: erro no video: " + e.getMessage());
        }

        //Handler to close the main window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //stop the timer and exit
                sTimer.stop();
                System.exit(0);
            }});

        //GUI:
        label = new JLabel("Send frame #        ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);
    }

    //------------------------------------
    //main
    //------------------------------------
    public static void main(String argv[]) throws Exception {

        /*
        * O servidor vai ter que se ligar a cada um do cliente através de uma thread
        * Deste modo ele vai poder reccer os pacotes e encaminhar para o sítio correto
        * Algo que ele também vai poder fazer (sendo este um point of presence) é mandar o vídeo para reprodução
        * */

        //get video filename to request:
        if (argv.length >= 1 ) {
            VideoFileName = argv[0];
            System.out.println("Servidor: VideoFileName indicado como parametro: " + VideoFileName);
        } else  {
            VideoFileName = "138Hitch.mov";
            System.out.println("Servidor: parametro não foi indicado. VideoFileName = " + VideoFileName);
        }

        File f = new File(VideoFileName);
        if (f.exists()) {
            //Create a Main object
            Server s = new Server();
            //show GUI: (opcional!)
            //s.pack();
            //s.setVisible(true);
        } else
            System.out.println("Ficheiro de video não existe: " + VideoFileName);
    }

    //------------------------
    //Handler for timer
    //------------------------
    /*public void actionPerformed(ActionEvent e) {

        //if the current image nb is less than the length of the video
        if (imagenb < VIDEO_LENGTH)
        {
            //update current imagenb
            imagenb++;

            try {
                //get next frame to send from the video, as well as its size
                int image_length = video.getnextframe(sBuf);

                //Builds an RTPpacket.java object containing the frame
                RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb*FRAME_PERIOD, sBuf, image_length);

                //get to total length of the full rtp packet to send
                int packet_length = rtp_packet.getlength();

                //retrieve the packet bitstream and store it in an array of bytes
                byte[] packet_bits = new byte[packet_length];
                rtp_packet.getpacket(packet_bits);

                //send the packet as a DatagramPacket over the UDP socket
                senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, RTP_dest_port);
                RTPsocket.send(senddp);

                System.out.println("Send frame #"+imagenb);
                //print the header bitstream
                rtp_packet.printheader();

                //update GUI
                //label.setText("Send frame #" + imagenb);
            }
            catch(Exception ex)
            {
                System.out.println("Exception caught: "+ex);
                System.exit(0);
            }
        }
        else
        {
            //if we have reached the end of the video file, stop the timer
            sTimer.stop();
        }*/


    public void actionPerformed(ActionEvent e) {
        System.out.println("actionPerformed: Preparando para enviar frame #" + imagenb);
        // Se o número da imagem atual é menor que o comprimento do vídeo
        if (imagenb < VIDEO_LENGTH) {
            // Atualiza o número da imagem atual
            imagenb++;

            try {
                // Obtém o próximo frame para enviar do vídeo, assim como seu tamanho
                int image_length = video.getnextframe(sBuf);

                // Cria um objeto RTPpacket com o frame
                RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb * FRAME_PERIOD, sBuf, image_length);

                // Obtém o comprimento total do pacote RTP
                int packet_length = rtp_packet.getlength();

                // Recupera o bitstream do pacote e armazena em um array de bytes
                byte[] packet_bits = new byte[packet_length];
                rtp_packet.getpacket(packet_bits);

                // Cria e envia o pacote como DatagramPacket pelo socket UDP
                senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, RTP_dest_port);
                System.out.println("actionPerformed: Preparando para enviar frame #" + imagenb);
                System.out.println("Enviando pacote para " + ClientIPAddr + " na porta " + RTP_dest_port);
                RTPsocket.send(senddp);
                System.out.println("Pacote enviado.");

                // Atualiza a GUI (opcional)
                // label.setText("Send frame #" + imagenb);
            } catch (Exception ex) {
                System.out.println("Exceção capturada: " + ex);
                ex.printStackTrace();
                System.exit(0);
            }
        } else {
            // Se alcançamos o final do vídeo, para o temporizador
            sTimer.stop();
        }
    }


}//end of Class Servidor
