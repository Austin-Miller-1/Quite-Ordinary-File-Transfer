package com.filetransfer.datatransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataTransferManagerTest {
    private DataTransferManager manager;


    @BeforeEach
    public void setupForEachTest(){
        manager = DataTransferManager.getInstance();
    }

    @Test
    public void testGetInstance_ConfirmSingleton(){
        DataTransferManager m = DataTransferManager.getInstance();
        assertSame(manager, m, "Instance retrieved must be same as any received instance" +
                " to be Singleton");
    }
}