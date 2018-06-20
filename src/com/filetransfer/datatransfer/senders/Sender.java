package com.filetransfer.datatransfer.senders;

import com.filetransfer.datatransfer.Message;

import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class Sender implements Runnable {
    private DatagramSocket sendSocket;

    public Sender(DatagramSocket sendSocket){
        this.sendSocket = sendSocket;
    }

    public abstract void sendMessage(Message message, InetAddress IPaddress);
}
