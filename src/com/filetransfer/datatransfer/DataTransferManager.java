package com.filetransfer.datatransfer;

import com.filetransfer.datatransfer.senders.Sender;

public class DataTransferManager {
    private static DataTransferManager instance;

    //Data belonging to the instance
    private Sender dataSender;
    //private Receiver dataReceiver;

    private DataTransferManager(){
        ; //TODO add meaningful constructor
    }

    public static DataTransferManager getInstance() {
        if(instance == null){
            instance = new DataTransferManager();
        }
        return instance;
    }
}
