package com.all.write.api;

public class Block {
    enum Type {
        SEND_FILE(0),
        GET_FILE(1),
        SEND_KEY(3);

        private int value;

        Type(int value) {
            this.value = value;
        }
    }
    Type type;
    String fileHash;
    String encFileHash;
    String privBlockHash;
    String authorSignature;
    String secretKey;
    String fileName;
    Long fileSize;
    String sender;
    String receiver;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getEncFileHash() {
        return encFileHash;
    }

    public void setEncFileHash(String encFileHash) {
        this.encFileHash = encFileHash;
    }

    public String getPrivBlockHash() {
        return privBlockHash;
    }

    public void setPrivBlockHash(String privBlockHash) {
        this.privBlockHash = privBlockHash;
    }

    public String getAuthorSignature() {
        return authorSignature;
    }

    public void setAuthorSignature(String authorSignature) {
        this.authorSignature = authorSignature;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
