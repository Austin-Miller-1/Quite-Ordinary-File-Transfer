package com.filetransfer;

import java.net.InetAddress;

public class Peer {
    private InetAddress IPAddress;
    private String peerHostname;

    public Peer(InetAddress IP, String hostname){
        IPAddress = IP;
        peerHostname = hostname;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getPeerHostname() {
        return peerHostname;
    }

    public void setPeerHostname(String peerHostname) {
        this.peerHostname = peerHostname;
    }
}
