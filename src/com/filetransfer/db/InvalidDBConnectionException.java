package com.filetransfer.db;

public class InvalidDBConnectionException extends Exception{
    public InvalidDBConnectionException(String s){
        super(s);
    }
}
