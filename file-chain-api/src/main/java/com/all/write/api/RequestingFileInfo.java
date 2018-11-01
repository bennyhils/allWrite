package com.all.write.api;

import com.all.write.NetworkMember;
import com.all.write.util.StringUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class RequestingFileInfo {
    private String originFilePath;
    private String hash;
    private String encFileHash;
    private Long fileSize;
    private NetworkMember sender;


    public static RequestingFileInfo createFileInfo(String filePath, NetworkMember sender){
        RequestingFileInfo fileInfo = new RequestingFileInfo();
        fileInfo.originFilePath = filePath;
        fileInfo.sender = sender;
        //TODO: проверка пути до файла
        Path path = Paths.get(filePath);
        try {
            fileInfo.hash = StringUtil.applySha256(Files.readAllBytes(path));
        } catch (IOException ignored) {

        }

        // todo ecnrypt
        fileInfo.encFileHash = fileInfo.hash;


        return fileInfo;
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

    public String getEncFileHash() {
        return encFileHash;
    }

    public void setEncFileHash(String encFileHash) {
        this.encFileHash = encFileHash;
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
                Objects.equals(encFileHash, that.encFileHash) &&
                Objects.equals(fileSize, that.fileSize) &&
                Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originFilePath, hash, encFileHash, fileSize, sender);
    }
}
