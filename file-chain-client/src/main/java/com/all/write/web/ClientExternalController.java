package com.all.write.web;

import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainExternal;
import com.all.write.core.StateHolder;
import com.all.write.core.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import com.all.write.api.rest.Response;
import com.all.write.core.DataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.LinkedList;

@Controller("clientExternalController")
public class ClientExternalController implements ChainExternal {

    @Autowired
    StateHolder stateHolder;

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
        chain.setChainLength(Long.valueOf(dataHolder.getBlocks().size()));
        chain.setLastBlockHash(dataHolder.getBlocks().isEmpty() ? "" :
                Utils.getHashOfBlock((Block)((LinkedList) dataHolder.getBlocks()).getLast()));
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

    @GetMapping("receiveFileRequest")
    @Override
    public void receiveFileRequest(RequestingFileInfo requestingFileInfo) {
        stateHolder.addRequestingFileInfo(requestingFileInfo);
    }

    @Override
    public void ackReceiveAndDownload(String fileHash) {

    }

    @Override
    public Boolean addBlock(Block block) {
        return null;
    }
}
