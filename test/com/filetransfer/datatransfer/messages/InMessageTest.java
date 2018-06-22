package com.filetransfer.datatransfer.messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InMessageTest {
    private final String TEST_MES = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras a justo lobortis, tempus tortor id, ornare ex. Aenean tincidunt, libero sed vehicula lacinia, eros lectus facilisis risus, nec varius nisi enim at elit. Quisque sit amet nibh id metus ultrices tempus. Ut ut lacinia odio. Aenean in pulvinar mauris. Proin mattis enim facilisis magna hendrerit posuere. Sed at orci et dui ornare laoreet. Suspendisse sed purus sit amet eros condimentum placerat non ac eros.\n" +
            "\n" +
            "Suspendisse posuere felis id magna viverra, ornare feugiat nisl luctus. Phasellus tempor orci nec turpis vestibulum, sit amet ultricies neque iaculis. Nam leo ante, tempus ut turpis in, tincidunt placerat lectus. Sed dignissim massa vitae eros fermentum convallis sed sed sem. Duis consequat quam quis neque semper dapibus. Vivamus id aliquam elit. Curabitur imperdiet ex et risus eleifend, ac interdum eros tempus. Donec ut fringilla erat. Phasellus eleifend vel libero ac feugiat. Proin commodo, est quis rutrum dignissim, est tellus auctor purus, sed efficitur massa mi non turpis. Proin imperdiet feugiat est, eget fringilla nunc vestibulum a. Vestibulum justo ipsum, consequat eu imperdiet a, vulputate sed nibh. Ut semper et neque at malesuada.";
    private final int MTU = 13;
    InMessage message;

    @BeforeEach
    public void setupBeforeEachTest(){
        message = new InMessage(TEST_MES);
    }

    @Test
    public void testGetFullMessage() {
        //Method to test
        String messageStr = message.getFullMessage();

        //Conditions
        assertNotNull(messageStr, "getFullMessage always returns String");
        assertEquals(TEST_MES, messageStr, "getFullMessage must return the exact & complete message" +
                " that it was originally given.");
    }

    @Test
    public void testGetByteSize(){
        //Test fixture
        int expectedBytes = TEST_MES.getBytes().length;

        //Method to test
        int actualBytes = message.getByteSize();

        //Condition
        assertEquals(expectedBytes, actualBytes, "getByteSize returns the exact number bytes" +
                " that the message requires");
    }

    @Test
    public void testGetBytes(){
        //Test fixture
        byte[] expectedBytes = TEST_MES.getBytes();

        //Method to test
        byte[] actualBytes = message.getBytes();

        assertNotNull(actualBytes, "getBytes always returns byte array");
        assertTrue(Arrays.equals(expectedBytes, actualBytes), "getBytes returns exact bytes of message");
    }

    @Test
    public void testAppend_appendSomeString(){
        //Method to test
        String appendedString = "Test Appending";
        message.append(appendedString);

        //Condition
        assertEquals(TEST_MES+appendedString, message.getFullMessage(), "append must append the exact String" +
                " to the existing message.");
    }

    @Test
    public void testAppend_appendNullString(){
        //Method to test
        message.append(null);

        //Condition
        assertEquals(TEST_MES, message.getFullMessage(), "append must not consider nulls as valid strings" +
                " and in doing so append characters to the message when given null");
    }

    @Test
    public void testIsComplete_incomplete(){
        //Method to test
        boolean completeness = message.isComplete();

        //Condition
        assertTrue(!completeness, "InMessage must not be complete until put into complete state");
    }

    @Test
    public void testIsComplete_complete(){
        //Test Fixture
        message.finish();

        //Method to test
        boolean completeness = message.isComplete();

        //Method to test
        assertTrue(completeness, "InMessage must be complete when put into complete state");
    }


}