package com.filetransfer.datatransfer;

public class MessageWriteWhileReadingException extends Exception{
    public MessageWriteWhileReadingException(String mes){
        super(mes);
    }
}
