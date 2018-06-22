package com.filetransfer.datatransfer.senders;

import com.filetransfer.Peer;
import com.filetransfer.datatransfer.messages.OutMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;

import static com.filetransfer.datatransfer.NetworkCommunicator.CRLF;
import static com.filetransfer.datatransfer.NetworkCommunicator.CRLF_BYTES;

/**
 * StopAndWaitSender is an implementation of a stop & wait protocol for UDP. It will send packets to its
 * receiver one-at-a-time. It sends one packet, waits for an ACK, then sends another. It is much slower than any
 * pipelined transport protocol because of this.
 */
public class StopAndWaitSender extends Sender {
    //CONSTANTS
    private static final int HEADERS_LENGTH = ("0 1 " + CRLF).length();
    private final int CHUNK_LENGTH = MSS - HEADERS_LENGTH;
    private final byte SPACE_CHAR_BYTE = (byte) ' ';
    //Transport layer data
    private boolean ACK, EOM;

    public StopAndWaitSender(OutMessage message, int sendFromPort, int sendToPort,
                             Peer sendToPeer) throws IncorrectSenderConfigurationException{
        super(message, sendFromPort, sendToPort, sendToPeer);
        ACK = false;
        EOM = false;
    }

    int getHeadersLength(){
        return HEADERS_LENGTH;
    }

    @Override
    byte[] getMessageChunk(){
        return message.getByteChunk(CHUNK_LENGTH);
    }

    //TODO testless. important enough?
    void updateHeaders(){
        ACK = !ACK; //flip ACK
        if(!message.hasMoreBytes()) EOM = true;
    }

    @Override
    byte[] attachTransportHeaders(byte[] chunk){
        byte[] ACKandEOM = new byte[]{boolToCharByte(ACK), SPACE_CHAR_BYTE, boolToCharByte(EOM), SPACE_CHAR_BYTE};

        int ACKandEOMLength = ACKandEOM.length;
        int chunkLength = chunk.length;
        int CRLFbytesLength = CRLF_BYTES.length;

        byte[] headeredChunk = new byte[ACKandEOMLength + chunkLength + CRLFbytesLength];
        System.arraycopy(ACKandEOM, 0, headeredChunk, 0, ACKandEOMLength);
        System.arraycopy(CRLF_BYTES, 0, headeredChunk, ACKandEOMLength, CRLFbytesLength);
        System.arraycopy(chunk, 0, headeredChunk, ACKandEOMLength + CRLFbytesLength, chunkLength);

        return headeredChunk;
    }

    @Override
    protected void createAndSendPacket(Peer peer, byte[] pktData) throws IOException {
        DatagramPacket pkt = new DatagramPacket(pktData, pktData.length, peer.getIPAddress(), sendToPort);
        sendSocket.send(pkt);
    }

    @Override
    protected boolean chunksRemain() {
        return message.hasMoreBytes();
    }

    @Override
    public void sendMessage(Peer peer) {

    }

    private byte boolToCharByte(boolean bool){
        return (byte) (bool?'1':'0');
    }
}
