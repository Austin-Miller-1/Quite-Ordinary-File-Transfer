package com.filetransfer.datatransfer.messages;

public class MessageWriteWhileReadingException extends Exception{
    public MessageWriteWhileReadingException(String mes){
        super(mes);
    }
}
