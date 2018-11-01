package com.all.write.api;

import java.util.Arrays;

public class Block {
    public enum Type {
        SEND_FILE(0),
        GET_FILE(1),
        SEND_KEY(3),
        GENESIS(4);

        private int value;

        public int getValue() {
            return value;
        }

        Type(int value) {
            this.value = value;
        }
    }
    private Type type;
    private String fileHash;
    private String encFileHash;
    private String privBlockHash;
    private byte[] authorSignature;
    private String secretKey;
    private String fileName;
    private Long fileSize;
    private String sender;
    private String senderAddress;
    private String receiver;
    private String receiverAddress;

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

    public byte[] getAuthorSignature() {
        return authorSignature;
    }

    public void setAuthorSignature(byte[] authorSignature) {
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

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    @Override
    public String toString() {
        return "Block{" +
                "type=" + type +
                ", fileHash='" + fileHash + '\'' +
                ", encFileHash='" + encFileHash + '\'' +
                ", privBlockHash='" + privBlockHash + '\'' +
                ", authorSignature=" + Arrays.toString(authorSignature) +
                ", secretKey='" + secretKey + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", sender=" + sender +
                ", senderAddress='" + senderAddress + '\'' +
                ", receiver=" + receiver +
                ", receiverAddress='" + receiverAddress + '\'' +
                '}';
    }
}
