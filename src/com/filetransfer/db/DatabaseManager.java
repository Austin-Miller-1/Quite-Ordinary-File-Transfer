package com.filetransfer.db;

import java.sql.*;

public class DatabaseManager {
    //Database and connectivity info
    private static final String PRODUCTION_DATABASE_NAME = "p2p-file-transfer-db";
    private static final String TEST_DATABASE_NAME = "test-db";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";
    private static final String DB_URL_PREFIX = "jdbc:h2:~/";
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static boolean isInProductionMode = true;
    private static Connection dbConnection = null;
    private static DatabaseManager dbInstance = null;

    /* Singleton code*/
    public static DatabaseManager getInstance() throws InvalidDBConnectionException {
        if(dbInstance == null){
            dbInstance = new DatabaseManager();
        }
        return dbInstance;

    }

    private DatabaseManager() throws InvalidDBConnectionException {
        initializeDatabase();
    }
    /* End Singleton code*/


    /**
     * Initializes the database containing the file and peer information. This initialization consists of the following
     * steps: registering the database driver, connecting to the database, creating the FILE table and creating the
     * PEER table.
     * @throws InvalidDBConnectionException Thrown when a connection to the database already exists (likely
     * another application is connected)
     */
    private void initializeDatabase() throws InvalidDBConnectionException {
        try {
            registerDriver();
            connectToDB();
            addPeerTable();
            addFileTable();
        }
        catch(JDBCDriverException | SQLException e){
            System.out.println(e.getMessage());
            //TODO Something else?
        }
    }

    /* START INITIALIZATION METHODS */

    /**
     * Registers the JDBC driver for the H2 database. Necessary first step in using the database.
     * @throws JDBCDriverException Thrown when JDBC driver is incorrect or if the H2 library is not properly configured.
     */
    private void registerDriver() throws JDBCDriverException {
        try{
            Class.forName(JDBC_DRIVER).getConstructor().newInstance();
        }catch(Exception e){
            String mes = "Could not use JDBC driver with name " + JDBC_DRIVER;
            throw new JDBCDriverException(mes);
        }
    }

    //Establishes connection with database

    /**
     * Establishes a connection to the database.
     * @throws InvalidDBConnectionException
     */
    private void connectToDB() throws InvalidDBConnectionException {
        try{
            String dbURL = DB_URL_PREFIX + (isInProductionMode?PRODUCTION_DATABASE_NAME:TEST_DATABASE_NAME);
            dbConnection = DriverManager.getConnection(dbURL, DB_USER, DB_PASS);
        }catch(Exception e){
            e.printStackTrace(); //TODO remove
            throw new InvalidDBConnectionException(e.getMessage());
        }
    }

    //Adds PEER table to database if it doesn't exist already
    private void addPeerTable() throws SQLException{
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
    private void addFileTable() throws SQLException{
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

    public boolean isDatabaseConnectionValid() {
        int timeToWaitMS = 1000;
        try{
            return dbConnection!=null && dbConnection.isValid(timeToWaitMS);
        }catch(SQLException e){
            return false;
        }
    }

    /* Methods for DB insertions*/

    public void addFile(String filePath, long fileSize, int peerID) throws SQLException, InvalidDBConnectionException{
        dbValidityCheck();

        //Create statement
        PreparedStatement stmt = dbConnection.prepareStatement("" +
                "INSERT INTO FILE VALUES (?, ?, ?)");
        stmt.setString(1, filePath);
        stmt.setLong(2, fileSize);
        stmt.setInt(3, peerID);

        //Execute
        runUpdate(stmt);
    }

    /* End insertion methods */

    /*DB statement helper methods*/
    void clear() throws SQLException, InvalidDBConnectionException{
        runUpdate("DELETE FROM FILE");
        runUpdate("DELETE FROM PEER");
    }

    void runUpdate(String statementString) throws SQLException, InvalidDBConnectionException{
        dbValidityCheck();
        runUpdate(dbConnection.prepareStatement(statementString));
    }

    void runUpdate(PreparedStatement statement) throws SQLException{
        statement.executeUpdate();
        statement.close();
    }

    ResultSet runQuery(String statementString) throws SQLException, InvalidDBConnectionException{
        dbValidityCheck();
        return runQuery(dbConnection.prepareStatement(statementString));
    }

    ResultSet runQuery(PreparedStatement statement) throws SQLException{
        ResultSet results = statement.executeQuery();
        statement.close();
        return results;
    }

    private void dbValidityCheck() throws InvalidDBConnectionException{
        if(!isDatabaseConnectionValid()){
            throw new InvalidDBConnectionException("DB connection doesn't exist " +
                    "or is invalid");
        }
    }

    /*End DB helper methods*/

    /* Test only methods */
    static void setToTestMode(){
        isInProductionMode = false;
    }


    /* End test-only methods */
}
