package com.all.write.api.rest;

import com.all.write.api.Block;
import com.all.write.api.RequestingFileInfo;
import org.springframework.http.ResponseEntity;

public interface ChainExternal {
    String ping();
    ResponseEntity pingExt();
    ResponseEntity getChain();
    void receiveFileRequest(RequestingFileInfo requestingFileInfo);
    void acceptUploadRequest(String fileHash);
    Boolean addBlock(Block block);
}
