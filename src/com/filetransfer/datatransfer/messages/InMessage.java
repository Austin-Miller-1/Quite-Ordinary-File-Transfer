package com.filetransfer.datatransfer.messages;

public class InMessage implements Message {
    private StringBuilder message;
    private boolean isMessageComplete;

    public InMessage(String mes){
        message = new StringBuilder(mes);
        isMessageComplete = false;
    }

    @Override
    public String getFullMessage() {
        return message.toString();
    }

    @Override
    public byte[] getBytes() {
        return getFullMessage().getBytes();
    }

    @Override
    public int getByteSize() {
        return getBytes().length;
    }

    /**
     * Appends the given String to the end of the current Message.
     * @param strToAppend String appended to end of Message. If null, Message is unchanged.
     */
    public void append(String strToAppend){
        if(strToAppend != null)
            message.append(strToAppend);
    }

    public boolean isComplete(){
        return isMessageComplete;
    }

    public void finish(){
        isMessageComplete = true;
    }
}
