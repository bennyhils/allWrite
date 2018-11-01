package com.all.write.api;

public class RequestingFileInfo {
    private String originFilePath;
    private String hash;
    private String hashOfEncrypted;
    private ExternalAddress sender;

    public String getOriginFilePath() {
        return originFilePath;
    }

    public void setOriginFilePath(String originFilePath) {
        this.originFilePath = originFilePath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHashOfEncrypted() {
        return hashOfEncrypted;
    }

    public void setHashOfEncrypted(String hashOfEncrypted) {
        this.hashOfEncrypted = hashOfEncrypted;
    }

    public ExternalAddress getSender() {
        return sender;
    }

    public void setSender(ExternalAddress sender) {
        this.sender = sender;
    }
}