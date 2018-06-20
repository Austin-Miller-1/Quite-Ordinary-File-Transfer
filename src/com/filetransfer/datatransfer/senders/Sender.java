package com.filetransfer.datatransfer.senders;

import com.filetransfer.Peer;
import com.filetransfer.datatransfer.Message;

import java.net.DatagramSocket;

public abstract class Sender implements Runnable {
    private static DatagramSocket sendSocket;

    private int sendFromPort;
    private int sendToPort;
    private Message message;
    private Peer peerToSendTo;

    protected Sender(Message message, int sendFromPort, int sendToPort, Peer peerToSendTo)
            throws IncorrectSenderConfigurationException{
        //Verify message
        if(message == null || message.getFullMessage().isEmpty())
            throw new IncorrectSenderConfigurationException("Message is invalid: " + message);

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

    public void run(){
        sendMessage(peerToSendTo);
    }

    public abstract void sendMessage(Peer peer);
}
