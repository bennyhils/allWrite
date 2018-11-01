package com.all.write.api;

public class LocalChainData {
    Long chainLength;
    String lastBlockHash;

    public Long getChainLength() {
        return chainLength;
    }

    public void setChainLength(Long chainLength) {
        this.chainLength = chainLength;
    }

    public String getLastBlockHash() {
        return lastBlockHash;
    }

    public void setLastBlockHash(String lastBlockHash) {
        this.lastBlockHash = lastBlockHash;
    }
}
