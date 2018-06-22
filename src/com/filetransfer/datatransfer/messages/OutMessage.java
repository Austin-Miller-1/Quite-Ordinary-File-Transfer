package com.filetransfer.datatransfer.messages;

import java.io.ByteArrayInputStream;

public class OutMessage implements Message {
    private ByteArrayInputStream messageByteStream;
    private int messageByteSize;

    public OutMessage(String message){
        byte[] messageBytes = message.getBytes();
        messageByteStream = new ByteArrayInputStream(messageBytes);
        messageByteSize = messageBytes.length;
    }

    @Override
    public String getFullMessage() {
        return new String(getBytes());
    }

    @Override
    public byte[] getBytes() {
        int currentStreamPos;
        int messageByteSize = getByteSize();
        byte[] allMessageBytes = new byte[messageByteSize];

        //Get current pos
        currentStreamPos = messageByteSize - messageByteStream.available();

        //Get bytes & correct pos
        messageByteStream.reset(); //Start at index 0
        messageByteStream.read(allMessageBytes, 0, getByteSize()); //read all (placing pos at end)
        messageByteStream.reset(); //Start at index 0 again
        messageByteStream.skip(currentStreamPos); //Place back at prev index

        //ret
        return allMessageBytes;
    }

    @Override
    public int getByteSize() {
        return messageByteSize;
    }

    public byte[] getByteChunk(int numOfBytes){
        //Invalid number of bytes, returns empty array
        if(numOfBytes <= 0) return new byte[]{};

        byte[] readBytes;
        int bytesToRead;

        //If enough bytes, return requested amount, otherwise, return exact number of bytes left
        if((bytesToRead = messageByteStream.available())>numOfBytes)
            readBytes = new byte[numOfBytes];
        else
            readBytes = new byte[bytesToRead];

        //Get byte chunk
        messageByteStream.read(readBytes, 0, readBytes.length);
        return readBytes;
    }

    public boolean hasMoreBytes(){
        return messageByteStream.available() > 0;
    }
}
