package com.filetransfer.datatransfer.messages;

public interface Message {
    String getFullMessage();
    byte[] getBytes();
    int getByteSize();
}
