package com.filetransfer.datatransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    private final String TEST_MES = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras a justo lobortis, tempus tortor id, ornare ex. Aenean tincidunt, libero sed vehicula lacinia, eros lectus facilisis risus, nec varius nisi enim at elit. Quisque sit amet nibh id metus ultrices tempus. Ut ut lacinia odio. Aenean in pulvinar mauris. Proin mattis enim facilisis magna hendrerit posuere. Sed at orci et dui ornare laoreet. Suspendisse sed purus sit amet eros condimentum placerat non ac eros.\n" +
            "\n" +
            "Suspendisse posuere felis id magna viverra, ornare feugiat nisl luctus. Phasellus tempor orci nec turpis vestibulum, sit amet ultricies neque iaculis. Nam leo ante, tempus ut turpis in, tincidunt placerat lectus. Sed dignissim massa vitae eros fermentum convallis sed sed sem. Duis consequat quam quis neque semper dapibus. Vivamus id aliquam elit. Curabitur imperdiet ex et risus eleifend, ac interdum eros tempus. Donec ut fringilla erat. Phasellus eleifend vel libero ac feugiat. Proin commodo, est quis rutrum dignissim, est tellus auctor purus, sed efficitur massa mi non turpis. Proin imperdiet feugiat est, eget fringilla nunc vestibulum a. Vestibulum justo ipsum, consequat eu imperdiet a, vulputate sed nibh. Ut semper et neque at malesuada.";
    private final int MTU = 13;
    private Message message;

    @BeforeEach
    public void setupBeforeEachTest(){
        message = new Message(TEST_MES); //Create test message
    }

    @Test
    public void testGetFullMessage(){
        //Condition
        String fullMes = message.getFullMessage();
        assertNotNull(fullMes, "getFullMessage always returns String");
        assertEquals(TEST_MES, fullMes, "getFullMessage must return the exact & complete message" +
                " that it was originally given.");
    }

    @Test
    public void testAppend_appendSomeString(){
        //Test Fixture
        String appendedString = "Test Appending";
        try {
            message.append(appendedString);
        } catch (MessageWriteWhileReadingException e) {
            fail("Should not cause read/write exception");
        }

        //Condition
        assertEquals(TEST_MES+appendedString, message.getFullMessage(), "appendMessage must append the exact String" +
                " to the existing message.");
    }

    @Test
    public void testAppend_appendNullString(){
        //Test Fixture
        try {
            message.append(null);
        } catch (MessageWriteWhileReadingException e) {
            fail("Should not cause read/write exception");
        }

        //Condition
        assertEquals(TEST_MES, message.getFullMessage());
    }
    @Test
    public void testGetByteChunk_firstChunk(){
        //Test Fixture
        byte[] expected = TEST_MES.substring(0, MTU).getBytes(); //Get first chunk bytes
        byte[] firstChunk = message.getByteChunk(MTU);

        //Condition
        assertNotNull(firstChunk, "getByteChunk always returns byte array");
        assertTrue(Arrays.equals(expected, firstChunk), "getByteChunk must give the requested bytes from beginning" +
                " of message");
    }

    @Test
    public void testGetByteChunk_invalidBytes(){
        //Test Fixture
        byte[] chunkA = message.getByteChunk(-10);
        byte[] chunkB = message.getByteChunk(0);

        //Condition
        assertNotNull(chunkA, "getByteChunk always returns byte array");
        assertNotNull(chunkB, "getByteChunk always returns byte array");
        assertTrue(chunkA.length == 0, "getByteChunk provides empty array " +
                "when requested negative num of bytes");
        assertTrue(chunkB.length == 0, "getByteChunk provides empty array " +
                "when requested zero bytes");
    }

    @Test
    public void testHasMoreBytes_doesHaveMore(){
        //Condition
        assertTrue(message.hasMoreBytes(), "hasMoreBytes must indicate that there is more message to" +
                " read if all bytes have not been read already");
    }

    @Test
    public void testHasMoreBytes_doesNotHaveMore(){
        //Test Fixture
        Message emptyMessage = new Message();

        //Condition
        assertFalse(emptyMessage.hasMoreBytes(), "hasMoreBytes must indicate that there is no more message to" +
                " read if no bytes remain to read.");

    }

    @Test
    public void testGetByteChunk_getAllChunks(){
        //Test Fixture
        byte[] currentlyExpectedBytes;
        byte[] currentlyReadBytes;

        //Condition
        int startIndex = 0;
        int endIndex = MTU;
        while(message.hasMoreBytes()){
            currentlyExpectedBytes = TEST_MES.substring(startIndex, endIndex).getBytes(); //Get chunk
            startIndex = endIndex; //Update indices
            endIndex+= MTU;
            if(endIndex > TEST_MES.length())
                endIndex = TEST_MES.length();

            //Get chunk from message
            currentlyReadBytes = message.getByteChunk(MTU);

            //Assert equals
            assertTrue(Arrays.equals(currentlyExpectedBytes, currentlyReadBytes), "getByteChunk must retrieve the proper" +
                    " bytes from any part of the message");
        }
    }

    @Test
    public void testGetByteChunk_cannotAddToMessageWhileReadingMessage(){
        //Test Fixture
        byte[] chunk = message.getByteChunk(MTU);

        //Condition
        try{
            message.append("test append");
            fail("Should not be able to append message while reading chunks of message");
        }
        catch(MessageWriteWhileReadingException e){
            ; //Expected exception, so pass test
        }
        catch(Exception e){
            e.printStackTrace();
            fail("Should not cause unrelated exception");
        }
    }
}