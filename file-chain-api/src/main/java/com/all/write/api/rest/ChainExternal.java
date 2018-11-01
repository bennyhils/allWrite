package com.all.write.api.rest;

import com.all.write.api.Block;
import com.all.write.api.RequestingFileInfo;

public interface ChainExternal {
    String ping();
    Response pingExt();
    Response getChain();
    void receiveFileRequest(RequestingFileInfo requestingFileInfo);
    void ackReceiveAndDownload(String fileHash);
    Boolean addBlock(Block block);
}
