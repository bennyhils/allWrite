package com.all.write.api;

public class FileDto {
    private String id;
    private String name;
    private Long size;
    private Double progress;
    private Long speed;
    private ExternalAddress sender;
    private ExternalAddress receiver;
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

    public ExternalAddress getSender() {
        return sender;
    }

    public void setSender(ExternalAddress sender) {
        this.sender = sender;
    }

    public ExternalAddress getReceiver() {
        return receiver;
    }

    public void setReceiver(ExternalAddress receiver) {
        this.receiver = receiver;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }
}
