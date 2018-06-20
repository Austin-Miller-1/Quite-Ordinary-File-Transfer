package com.filetransfer.datatransfer.senders;

public class IncorrectSenderConfigurationException extends Exception {
    public IncorrectSenderConfigurationException(String mes){
        super(mes);
    }
}
