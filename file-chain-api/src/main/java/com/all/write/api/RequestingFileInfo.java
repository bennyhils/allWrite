package com.all.write.api;

import com.all.write.NetworkMember;

public class RequestingFileInfo {
    private String originFilePath;
    private String hash;
    private String hashOfEncrypted;
    private Long fileSize;
    private NetworkMember sender;

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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public NetworkMember getSender() {
        return sender;
    }

    public void setSender(NetworkMember sender) {
        this.sender = sender;
    }
}
