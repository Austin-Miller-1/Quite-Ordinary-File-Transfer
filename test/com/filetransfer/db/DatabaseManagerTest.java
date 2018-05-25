package com.filetransfer.db;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseManagerTest {

    @Test
    public void testInitializeDatabase_oneConnectionInitializedProperly() throws MultipleConnectionException {
        //Setup

        //Try
        DatabaseManager.initializeDatabase(); //May throw exception

        //Assert
        assertTrue(DatabaseManager.isDatabaseConnectionValid(),
                "Database connection must be valid (e.g. is not null)"); //Database connection is valid
    }

    @Test
    @Disabled(value="Need a way to test to ensure multiple connections are not allowed - Currently not implemented")
    public void testInitializeDatabase_simultaneousConnectionsAreNotAllowed() throws MultipleConnectionException {
        //Set up
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseManager.initializeDatabase();
                } catch (MultipleConnectionException e) {
                    throw new RuntimeException("Should not be error on first initialization");
                }
            }
        });
        t.start();

        Executable codeToRun = () -> DatabaseManager.initializeDatabase();

        //Try and assert
        assertThrows(MultipleConnectionException.class, codeToRun,"Multiple connections to " +
                "database aren't allowed & cause exception");
    }

}