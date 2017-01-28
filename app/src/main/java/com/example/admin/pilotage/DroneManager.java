package com.example.admin.pilotage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the network communications with the drone.
 * It will intialize the socket used to recieve the NavData from the drone and send commands to the drone.
 * @see Pilotage
 */
public class DroneManager {

    /**
     * Socket used to handle all the communications
     */
    public DatagramSocket udp_socket;
    /**
     * IP adress of the AR.Drone 2.0
     * @see DroneManager#adress
     */
    public InetAddress adress;
    /**
     * IP Adress of the drone in a string
     * @value "192.168.1.1"
     */
    String host = "192.168.1.1";

    // These are used to SEND the commands
    public DatagramPacket udp_packet_cmd;
    public byte[] buffer_cmd;
    String strCmd = "AT*LED=1,4,1056964608,0"; // Random command. It won't be processed. LED command was choosed for safety reasons anyway.
    Integer cmd_port = 5556;
    Thread tCom_UDP;

    // NAVDATA PACKET
    public byte[] nav_buff_start = {0x01, 0x00, 0x00, 0x00};
    public byte[] nav_buff = new byte[512];
    public byte[] at_buff = new byte[512];
    public DatagramPacket nav_packet;
    public DatagramPacket at_packet;
    Integer nav_socket = 5554;
    String CommandeAT = "AT*CONFIG=2,\"general:navdata_demo\",\"TRUE\"\r"; // Initializes the continuous emission of navdata

    // VIDEO PACKET
    public byte[] at_Video = {0x01, 0x00, 0x00, 0x00};
    public DatagramPacket video_packet;
    Integer video_port = 5556;

    // CONTROL PACKET
    Integer port_controle = 5559;
    public byte[] ctrl_buff = new byte[512];
    public byte[] ctrl_start = {0x01, 0x00, 0x00, 0x00};
    public DatagramPacket ctrl_packet;


    // Thread exécuté une seule fois pour init.
    boolean bInit = false;

    public DroneManager() {

        // Creating a socket and the packets. It is only done once.

        buffer_cmd = new byte[512];

        // CREATING THE SOCKET
        try {
            udp_socket = new DatagramSocket();

        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // INITIALIZING IP ADRESS
        try {
            adress = InetAddress.getByName(host);
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // GENERATING PACKETS BUFFERS
        try {
            buffer_cmd = strCmd.concat("\r").getBytes("ASCII"); // To send data
            at_buff = CommandeAT.getBytes("ASCII"); // To recieve navdata

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        // COMMAND
        udp_packet_cmd = new DatagramPacket(buffer_cmd, buffer_cmd.length, adress, cmd_port);

        // NAVDATA
        nav_packet = new DatagramPacket(nav_buff_start, nav_buff_start.length, adress, nav_socket);
        at_packet = new DatagramPacket(at_buff, at_buff.length, adress, cmd_port);

        // VIDEO
        video_packet = new DatagramPacket(at_Video, at_Video.length, adress, video_port);

        // CONTROL
        ctrl_packet = new DatagramPacket(ctrl_start, ctrl_start.length,adress,5559);

        bInit = false;

        DemarreThread();


    }

    /**
     * Used to send the AT command into the command socket of the drone (socket 5556)
     * @param strCommand The AT command to send.
     * @author Jules Simon
     */
    public void SendCMD(String strCommand) {

        // Updating the buffer with strCommand

        try {
            buffer_cmd = strCommand.concat("\r").getBytes("ASCII");

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        udp_packet_cmd.setData(buffer_cmd); // Loading the command into the buffer

    }

    /**
     * Networking thread. It will be called every time a command has to be sent.
     */
    class TComUDP implements Runnable {

        public void run() {

            do {

                if (bInit == false) {

                    // These commands are performed only once to initialize the communication.
                    try {
                        // We send a random command to the Navdata socket (5554) (here the command is 1,0,0,0) to start the communication on this socket.
                        udp_socket.send(nav_packet);
                        TimeUnit.MILLISECONDS.sleep(20);
                        // We send the command used to start continuous navdata emission.
                        udp_socket.send(at_packet);
                        TimeUnit.MILLISECONDS.sleep(20);
                        // We send a random command on the video stream socket (here the command is 1,0,0,0) to start the video stream on this socket.
                        udp_socket.send(video_packet);
                        TimeUnit.MILLISECONDS.sleep(20);
                        // We send a random command on the control socket (here the command is 1,0,0,0) to start the control socket communication.
                        udp_socket.send(ctrl_packet);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // NAVDATA
                    nav_packet.setData(nav_buff);
                    nav_packet.setLength(nav_buff.length);
                    // CONTROL
                    ctrl_packet.setData(ctrl_buff);
                    ctrl_packet.setLength(ctrl_buff.length);

                    bInit = true;
                } else {
                    // We send the commands and recieve the buffer at the same time to save time.
                    try {
                        udp_socket.send(udp_packet_cmd);
                        udp_socket.receive(nav_packet);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }while(true);
        }
    };

    /**
     * Retrieves Navdata from socket 5554
     * @return The whole navdata buffer in a byte array.
     */
    public byte[] GetBuffer(){
        byte[] buffer;
        buffer = nav_buff;
        return buffer;
    }

    /**
     * Starts the networking thread used to send the commands and receive navdata.
     * @see DroneManager#SendCMD(String)
     */
    public void DemarreThread(){

        tCom_UDP = new Thread(new TComUDP());
        tCom_UDP.start();
    }


}

