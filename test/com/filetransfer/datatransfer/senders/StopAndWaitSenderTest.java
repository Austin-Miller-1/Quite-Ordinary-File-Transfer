package com.filetransfer.datatransfer.senders;

import com.filetransfer.Peer;
import com.filetransfer.datatransfer.NetworkCommunicator;
import com.filetransfer.datatransfer.messages.OutMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static com.filetransfer.datatransfer.NetworkCommunicator.CRLF;
import static org.junit.jupiter.api.Assertions.*;

class StopAndWaitSenderTest {
    private final String MESSAGE_STRING = "Okay, so that test message wasn't that great, so here's an even better one that's less redundant and therefore is EASIER TO USE. Still sent from past me to future me.";
    private static final int FROM_PORT = 50001, TO_PORT = 50002;

    private StopAndWaitSender sender;
    private OutMessage message;
    private Peer peer;
    private static DatagramSocket receiverSocket;
    private int MSS;

    //Chunk related
    private int chunkSize;
    private int numberOfChunks;
    @BeforeAll
    public static void setupBeforeAllTests(){
        try {
            Sender.setSendSocket(new DatagramSocket(FROM_PORT));
            receiverSocket = new DatagramSocket(TO_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            fail("Socket should have no issue with valid port");
        }
    }

    @BeforeEach
    public void setupBeforeEachTest(){
        //Sender config.
        MSS = 30;
        Sender.setMSS(MSS);

        //Sender data
        message = new OutMessage(MESSAGE_STRING);
        System.out.println(new DatagramPacket(new byte[1], 1).getAddress()); //todo remove
        peer = new Peer(new DatagramPacket(new byte[1], 1).getAddress(), "test-host");

        //Create sender
        try{
            sender = new StopAndWaitSender(message, FROM_PORT, TO_PORT, peer);
        }
        catch(IncorrectSenderConfigurationException e){
            e.printStackTrace();
            fail("Must allow valid configuration without exception");
        }

        //Set vars
        chunkSize = MSS - sender.getHeadersLength();
        numberOfChunks = (MESSAGE_STRING.length()+chunkSize-1)/chunkSize; //integer div of (mes.length/chunklength), but rounds up
    }

    @Test
    public void testSendMessage(){
        fail("Unable to implement with current testing-knowledge -- sendMessage method requires a Receiver class" +
                " that recreates the message from packets and sends ACKs back. If I'm to make a test object" +
                " that does this, I'd basically be making the exact Receiver the app needs. This means that" +
                " unit testing on this method doesn't seem good. However, integration testing would not test" +
                " 'sendMessage' alone, but also the Receiver functionality. Is unit testing possible here?");
    }

    @Test
    public void testGetHeadersLength(){
        String expectedHeader = "0 0 " + CRLF;
        int expectedHeaderLength = expectedHeader.length();

        //Method to test
        int actualHeaderLength = sender.getHeadersLength();

        assertEquals(expectedHeaderLength, actualHeaderLength, "getHeaderLength must always produce" +
                " the length of the headers used in the packet. For StopAndWaitSenders, this value must be constant");
    }

    @Test
    public void testGetMessageChunk_allChunksRetrievedProperly(){
        //Verify that each chunk is valid and expected
        for(int currentChunkNum = 0; currentChunkNum < numberOfChunks; currentChunkNum++){
            int lastCharIndex = (currentChunkNum == numberOfChunks-1)?MESSAGE_STRING.length():(currentChunkNum+1)*chunkSize;

            String currentExpectedChunk = MESSAGE_STRING.substring(
                    currentChunkNum*chunkSize, lastCharIndex);
            String currentActualChunk = new String(sender.getMessageChunk());

            assertNotNull(currentActualChunk, "getMessageChunk must always return byte array");
            assertEquals(currentExpectedChunk, currentActualChunk, "getMessageChunk must always get expected " +
                    " chunk for whole message.");
        }
    }

    @Test
    public void testAttachTransportHeaders_checkEachChunk(){
        //Verify that each chunk is given the expected header
        int ACK = 0, EOM = 0;
        for(int currentChunkNum = 0; currentChunkNum < numberOfChunks; currentChunkNum++){
            //update flags
            if(currentChunkNum == numberOfChunks-1) EOM = 1;
            ACK = (ACK == 0)?1:0;

            String currentChunk = new String(sender.getMessageChunk());
            sender.updateHeaders(); //TODO remove dependency from test???

            String expectedHeaderedChunk = ACK + " " + EOM + " " + CRLF + currentChunk;

            byte[] headeredChunkBytes = sender.attachTransportHeaders(currentChunk.getBytes());
            String actualHeaderedChunk = new String(headeredChunkBytes);

            assertNotNull(headeredChunkBytes, "attachHeaders must always return byte array");
            assertEquals(expectedHeaderedChunk, actualHeaderedChunk, "attachHeaders must attach the " +
                    " appropriate flags (ACK & EOM) for each chunk of a message");
        }
    }

    @Test
    public void testCreateAndSendPacket(){
        //test method
        try{
            sender.createAndSendPacket(peer, MESSAGE_STRING.getBytes());
        }catch(Exception e){
            fail("createAndSendPacket must not cause an exception when sending packet");
        }

        //Condition
        DatagramPacket receivedPacket = new DatagramPacket(new byte[MESSAGE_STRING.getBytes().length], MESSAGE_STRING.getBytes().length);

        try{
            receiverSocket.receive(receivedPacket);
        }catch(Exception e){
            e.printStackTrace();
            fail("createAndSendPacket must not cause exception when sending packet on receiver side");
        }

        assertEquals(MESSAGE_STRING, receivedPacket.getData(), "createAndSendPacket must send all" +
                " expected data via packet.");
        assertEquals(FROM_PORT, receivedPacket.getPort(), "createAndSendPacket must send packet from" +
                " established port");

    }

}