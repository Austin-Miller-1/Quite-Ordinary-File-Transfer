package com.filetransfer.datatransfer.senders;

import com.filetransfer.Peer;
import com.filetransfer.datatransfer.messages.OutMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

//TODO Use Simple Builder pattern for validations
public abstract class Sender implements Runnable {
    public static final int DEFAULT_MSS = 1024;
    public static final int UDP_HEADER = 8;
    public static final int IP_HEADER = 20;

    //TODO following fields MUST be set for app to work BUT setting after once is dangerous; subclasses SHOULD NOT
    //have ability to change. Figure out better design.
    protected static DatagramSocket sendSocket;
    protected static int MSS = DEFAULT_MSS;
    protected static int MTU = MSS - UDP_HEADER - IP_HEADER;

    protected int sendFromPort;
    protected int sendToPort;
    protected OutMessage message;
    protected Peer peerToSendTo;

    protected Sender(OutMessage message, int sendFromPort, int sendToPort, Peer peerToSendTo)
            throws IncorrectSenderConfigurationException{
        //Verify message
        if(message == null || message.getFullMessage().isEmpty())
            throw new IncorrectSenderConfigurationException("Message is invalid: " + message);

        //Verify MSS & MTU
        if(MTU <= 0)
            throw new IncorrectSenderConfigurationException("MTU is too small: " + MTU);

        //Verify ports
        if(sendFromPort <= 0 || sendToPort <= 0)
            throw new IncorrectSenderConfigurationException("Ports are invalid; Attempted to " +
                    "send from port " + sendFromPort + " to port " + sendToPort);

        //Verify send socket
        if(sendSocket == null || sendSocket.isClosed())
            throw new IncorrectSenderConfigurationException("Socket is not set up properly " + sendSocket);

        //Verify peer
        if(peerToSendTo == null)
            throw new IncorrectSenderConfigurationException("Invalid Peer " + peerToSendTo);

        //Given that configuration is correct, create sender
        this.message = message;
        this.sendFromPort = sendFromPort;
        this.sendToPort = sendToPort;
        this.peerToSendTo = peerToSendTo;
    }

    public static void setSendSocket(DatagramSocket socket){
        sendSocket = socket;
    }

    public static void setMSS(int bytes){
        MSS = bytes;
        MTU = bytes - UDP_HEADER - IP_HEADER;
    }

    public static void setMTU(int bytes){
        MTU = bytes;
        MSS = bytes + UDP_HEADER + IP_HEADER;
    }

    public void run(){
        try{
            sendMessage(peerToSendTo);
        }catch(CouldNotSendPacketException e){
            System.out.println("Message unable to send");
        }
    }

    //todo not tested, no acks
    public void sendMessage(Peer peer) throws CouldNotSendPacketException{
        while(chunksRemain()){
            try{
                createAndSendPacket(peer, attachTransportHeaders(getMessageChunk()));
            }catch(IOException e){
                System.out.println("Error sending packet... Stopping process");
                e.printStackTrace();
                throw new CouldNotSendPacketException("Packet failed to send");
            }
        }
    }

    abstract boolean chunksRemain();
    abstract void createAndSendPacket(Peer peer, byte[] pktData) throws IOException;
    abstract byte[] attachTransportHeaders(byte[] messageChunk);
    abstract byte[] getMessageChunk();
}
