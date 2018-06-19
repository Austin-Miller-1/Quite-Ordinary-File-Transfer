package com.filetransfer;

public class PeerFile {
    private String filePath;
    private long bytes; //Can accurately (to the byte) represent file size for all files under 8192 petabytes

    public PeerFile(String filePath, long bytes) {
        this.filePath = filePath;
        this.bytes = bytes;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }
}
