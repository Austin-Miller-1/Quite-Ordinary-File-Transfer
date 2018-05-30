package com.filetransfer.db;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    static DatabaseManager db = null;

    @BeforeAll
    public static void beforeTestingSetup(){
        DatabaseManager.setToTestMode();
        try {
            db = DatabaseManager.getInstance();
        }catch(InvalidDBConnectionException e){
            unexpectedExceptionFail(e, "Invalid DB connection");
        }
    }

    @BeforeEach
    public void beforeEachTestSetup(){
        try{
            db.clear(); //Remove all files from database
        }catch(SQLException e){
            unexpectedExceptionFail(e, "Invalid SQL");
        }
        catch(InvalidDBConnectionException e){
            unexpectedExceptionFail(e, "Invalid DB connection");
        }
    }

    @Test
    public void testGetInstance_Singleton(){
        try {
            DatabaseManager testDB = DatabaseManager.getInstance();
            assertSame(db, testDB);
        }catch(InvalidDBConnectionException e){
            unexpectedExceptionFail(e, "Invalid DB connection");
       }
    }

    @Test
    public void testInitializeDatabase(){
        assertTrue(db.isDatabaseConnectionValid(),
                "Database connection must be valid (e.g. is not null)"); //Database connection is valid
    }

    @Test
    public void testAddFile_shouldAddValidFile(){
        //First, add valid file
        String filePath = "C\\test.jpg";
        long fileSize = 400;
        int peerID = 1;
        try {
            db.addFile(filePath, fileSize, peerID);
        }
        catch(SQLException | InvalidDBConnectionException e){
            unexpectedExceptionFail(e, "Inserting unqiue, valid file shouldn't cause SQL or DB exception");
        }

        //Second, check to see that file has been added
        ResultSet results;
        try {
            results = db.runQuery(String.format("SELECT COUNT(*) FROM FILE " +
                    "WHERE FILEPATH = %s AND FILESIZE = %d " +
                    "AND PEERID = %d", filePath, fileSize, peerID));
            results.next();
            int rowCount = results.getInt(1);
            assertEquals(1, rowCount, "Valid file should be added to database");

        }
        catch (SQLException | InvalidDBConnectionException e) {
            unexpectedExceptionFail(e, "Querying valid file shouldn't cause SQL or DB exception");
        }
    }

    @Test
    public void testAddFile_shouldntAddDuplicateFile(){
        //First, add valid file
        String filePath = "C\\test.jpg";
        long fileSize = 400;
        int peerID = 1;
        try {
            db.addFile(filePath, fileSize, peerID);
        }
        catch(SQLException | InvalidDBConnectionException e){
            unexpectedExceptionFail(e, "Inserting valid file shouldn't cause SQL or DB exception");
        }

        //Then, add same file. Expect SQL exception
        try{
            db.addFile(filePath, fileSize, peerID);
            fail("Adding duplicate file should not be allowed");
        }
        catch(InvalidDBConnectionException e){
            unexpectedExceptionFail(e, "Inserting invalid file shouldn't cause DB connection exception");
        }
        catch(SQLException e){
            ; //Success scenario
        }
    }

    @Test
    public void testAddFile_shouldntAddFileWithoutPeer(){
        String filePath = "C\\test.jpg";
        long fileSize = 400;
        int invalidPeer = -1;
        try{
            db.addFile(filePath, fileSize, invalidPeer);
            fail("Should not be allowed to add file associated with invalid peer");
        }
        catch(InvalidDBConnectionException e){
            unexpectedExceptionFail(e, "Adding file associated with invalid peer " +
                    "should not cause DB connection exception");
        }
        catch(SQLException e){
            ; //Successful scenario
        }

    }

    /* Test helper methods */
    private static void unexpectedExceptionFail(Exception e, String testMes){
        e.printStackTrace();
        fail(testMes);
    }
}