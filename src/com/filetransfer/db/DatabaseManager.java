package com.filetransfer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    //Database and connectivity info
    private static final String DATABASE_NAME = "p2p-file-transfer-db";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";
    private static final String DB_URL = "jdbc:h2:~/"+DATABASE_NAME;
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static Connection dbConnection = null;

    public static void initializeDatabase() throws MultipleConnectionException{
        try {
            registerDriver();
            connectToDB();
            addFileTable();
            addPeerTable();
        }
        catch(JDBCDriverException | SQLException e){
            System.out.println(e.getMessage());
            //TODO Something else?
        }
    }

    /* START INITIALIZATION METHODS */
    //Registers JDBC driver
    private static void registerDriver() throws JDBCDriverException {
        try{
            Class.forName(JDBC_DRIVER).getConstructor().newInstance();
        }catch(Exception e){
            String mes = "Could not use JDBC driver with name " + JDBC_DRIVER;
            throw new JDBCDriverException(mes);
        }
    }

    //Establishes connection with database
    private static void connectToDB() throws MultipleConnectionException {
        try{
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }catch(Exception e){
            //e.printStackTrace(); //TODO remove
            throw new MultipleConnectionException(e.getMessage());
        }
    }

    //Adds PEER table to database if it doesn't exist already
    private static void addPeerTable() throws SQLException{
            PreparedStatement stmt = dbConnection.prepareStatement(""+
                    "CREATE TABLE IF NOT EXISTS PEER(\n"+
                    "peerID  INT NOT NULL AUTO_INCREMENT,\n" +
                    "hostName    VARCHAR(50) NOT NULL,\n" +
                    "IP  VARCHAR(16) NOT NULL,\n" +
                    "PRIMARY KEY(peerID),\n" +
                    "UNIQUE(hostname, IP)\n" +
                    ");");
            stmt.executeUpdate();
            stmt.close();

    }

    //Adds FILE table to database if it doesn't exist already
    private static void addFileTable() throws SQLException{
            PreparedStatement stmt = dbConnection.prepareStatement(""+
                    "CREATE TABLE IF NOT EXISTS FILE(\n" +
                    "filePath  VARCHAR(100) NOT NULL,\n" +
                    "fileSize  INTEGER   NOT NULL,\n" +
                    "hostPeerID  INTEGER NOT NULL,\n" +
                    "PRIMARY KEY(filePath, fileSize, hostPeerId),\n" +
                    "FOREIGN KEY(hostPeerID) REFERENCES PEER(peerID)\n" +
                    ");");
            stmt.executeUpdate();
            stmt.close();
    }
    /* END INITIALIZATION METHODS*/

    public static boolean isDatabaseConnectionValid() {
        int timeToWaitMS = 1000;
        try{
            return dbConnection!=null && dbConnection.isValid(timeToWaitMS);
        }catch(SQLException e){
            return false;
        }
    }
}
