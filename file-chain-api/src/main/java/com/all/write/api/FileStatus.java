package com.all.write.api;

public enum FileStatus {
    TRANSFER("TRANSFER"),
    IN_PROGRESS("IN_PROGRESS"),
    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    private String value;

    FileStatus(String value) {
        this.value = value;
    }
}
