package com.all.write.api;

import com.all.write.NetworkMember;

public class FileDto {
    private String id;
    private String name;
    private Long size;
    private Double progress;
    private Long speed;
    private NetworkMember sender;
    private NetworkMember receiver;
    private FileStatus fileStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public NetworkMember getSender() {
        return sender;
    }

    public void setSender(NetworkMember sender) {
        this.sender = sender;
    }

    public NetworkMember getReceiver() {
        return receiver;
    }

    public void setReceiver(NetworkMember receiver) {
        this.receiver = receiver;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }
}
