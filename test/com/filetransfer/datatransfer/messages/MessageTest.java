package com.filetransfer.datatransfer.messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    private final String TEST_MES = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras a justo lobortis, tempus tortor id, ornare ex. Aenean tincidunt, libero sed vehicula lacinia, eros lectus facilisis risus, nec varius nisi enim at elit. Quisque sit amet nibh id metus ultrices tempus. Ut ut lacinia odio. Aenean in pulvinar mauris. Proin mattis enim facilisis magna hendrerit posuere. Sed at orci et dui ornare laoreet. Suspendisse sed purus sit amet eros condimentum placerat non ac eros.\n" +
            "\n" +
            "Suspendisse posuere felis id magna viverra, ornare feugiat nisl luctus. Phasellus tempor orci nec turpis vestibulum, sit amet ultricies neque iaculis. Nam leo ante, tempus ut turpis in, tincidunt placerat lectus. Sed dignissim massa vitae eros fermentum convallis sed sed sem. Duis consequat quam quis neque semper dapibus. Vivamus id aliquam elit. Curabitur imperdiet ex et risus eleifend, ac interdum eros tempus. Donec ut fringilla erat. Phasellus eleifend vel libero ac feugiat. Proin commodo, est quis rutrum dignissim, est tellus auctor purus, sed efficitur massa mi non turpis. Proin imperdiet feugiat est, eget fringilla nunc vestibulum a. Vestibulum justo ipsum, consequat eu imperdiet a, vulputate sed nibh. Ut semper et neque at malesuada.";
    private final int MTU = 13;
    private BadMessage message;

    @BeforeEach
    public void setupBeforeEachTest(){
        message = new BadMessage(TEST_MES); //Create test message
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