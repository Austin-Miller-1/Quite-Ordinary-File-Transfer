package com.filetransfer.datatransfer;

import java.io.ByteArrayInputStream;

/*TODO Delegate functionality to separate classes, 'MessageReceived' and 'MessageToSend'
    this is because the purposes of Message are ENTIRELY different based on where the message is used.
    Accommodating for both purposes makes the Message class very confusing and unnecessarily complex.
*/
public class Message {
    private static final int MIN_MESSAGE_CHAR_SIZE = 1024;
    private StringBuilder message = new StringBuilder(MIN_MESSAGE_CHAR_SIZE);

    //Reading components
    private ByteArrayInputStream messageByteStream = null;

    /**
     * Creates new Message starting with provided string.
     * @param startMes Start of the Message.
     */
    public Message(String startMes){
        try{
            append(startMes);
        }catch(MessageWriteWhileReadingException e){
            message = new StringBuilder(startMes);
        }
    }

    /**
     * Creates a new message. Although empty, it still allocates a default amount of buffer space for future.
     */
    public Message(){
        ;
    }

    /**
     * Returns complete Message as String.
     * @return The Message as a String.
     */
    public String getFullMessage(){
        return message.toString();
    }

    /**
     * Appends the given String to the end of the current Message.
     * @param strToAppend String appended to end of Message. If null, Message is unchanged.
     * @throws MessageWriteWhileReadingException if a string is provided to append to message while message is currently being read
     */
    public void append(String strToAppend) throws MessageWriteWhileReadingException{
        if(strToAppend != null) {
            if(isCurrentlyBeingRead()) {
                throw new MessageWriteWhileReadingException("Cannot append while message is being read");
            } else {
                message.append(strToAppend);
            }
        }
    }

    public byte[] getByteChunk(int numOfBytes){
        //Invalid number of bytes, returns empty array
        if(numOfBytes <= 0) return new byte[]{};

        //First time stream set up (lazy approach)
        byteStreamSetup();

        byte[] readBytes;
        int bytesToRead;
        //TODO inefficiency to retrieve each time, could manage value in class
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
        byteStreamSetup();
        return messageByteStream.available() > 0;
    }

    private void byteStreamSetup(){
        if(messageByteStream == null)
            messageByteStream = new ByteArrayInputStream(message.toString().getBytes());
    }

    private boolean isCurrentlyBeingRead(){
        return messageByteStream != null;
    }
}
