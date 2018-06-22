package com.filetransfer.datatransfer.senders;

public class CouldNotSendPacketException extends Exception {
    public CouldNotSendPacketException(String mes){
        super(mes);
    }
}
