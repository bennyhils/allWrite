package com.all.write.api;

import com.all.write.NetworkMember;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class RequestingFileInfo {
    private String originFilePath;
    private String hash;
    private String hashOfEncrypted;
    private Long fileSize;
    private NetworkMember sender;


    public static RequestingFileInfo createFileInfo(String path){
        RequestingFileInfo fileInfo = new RequestingFileInfo();
        fileInfo.originFilePath = path;

        return null;
    }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestingFileInfo)) return false;
        RequestingFileInfo that = (RequestingFileInfo) o;
        return Objects.equals(originFilePath, that.originFilePath) &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(hashOfEncrypted, that.hashOfEncrypted) &&
                Objects.equals(fileSize, that.fileSize) &&
                Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originFilePath, hash, hashOfEncrypted, fileSize, sender);
    }
}
