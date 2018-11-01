package com.all.write.api.rest;

import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.api.RequestingFileInfo;

import java.util.List;

public interface ChainExternal {
    void ping();
    LocalChainData pingExt();
    List<Block> getChain();
    void receiveFileRequest(RequestingFileInfo requestingFileInfo);
    void ackReceiveAndDownload(String fileHash);
}
