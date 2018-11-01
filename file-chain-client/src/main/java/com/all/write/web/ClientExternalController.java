package com.all.write.web;

import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainExternal;
import com.all.write.core.DataHolder;
import com.all.write.core.StateHolder;
import com.all.write.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.LinkedList;

@Controller("clientExternalController")
public class ClientExternalController implements ChainExternal {

    @Autowired
    private StateHolder stateHolder;

    @Autowired
    private DataHolder dataHolder;

    @PostConstruct
    public void init() {
        System.out.println("externalController init");
    }

    @GetMapping("/ping")
    @ResponseBody
    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public ResponseEntity pingExt() {
        LocalChainData chain = new LocalChainData();
        chain.setChainLength((long) dataHolder.getBlocks().size());
        chain.setLastBlockHash(dataHolder.getBlocks().isEmpty() ? "" :
                StringUtil.getHashOfBlock((Block)((LinkedList) dataHolder.getBlocks()).getLast()));
        return new ResponseEntity<>(chain,
                HttpStatus.OK);
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/chain", method = RequestMethod.GET)
    public ResponseEntity getChain() {
        return new ResponseEntity<>(dataHolder.getBlocks().toArray(),
                HttpStatus.OK);
    }

    @PostMapping("/receiveFileRequest")
    @Override
    public void receiveFileRequest(RequestingFileInfo requestingFileInfo) {
        stateHolder.addRequestingFileInfo(requestingFileInfo);
    }

    @Override
    public void acceptUploadRequest(String fileHash) {
        //call download
        RequestingFileInfo outgoingInfo = stateHolder.getOutgoingRequest(fileHash);
        File file = new File(outgoingInfo.getOriginFilePath());
    }

    @Override
    public Boolean addBlock(Block block) {
        return null;
    }
}
