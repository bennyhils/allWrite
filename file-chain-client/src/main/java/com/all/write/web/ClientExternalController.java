package com.all.write.web;

import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainExternal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller("clientExternalController")
public class ClientExternalController implements ChainExternal {

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
    public LocalChainData pingExt() {
        return null;
    }

    @Override
    public List<Block> getChain() {
        return null;
    }

    @Override
    public void receiveFileRequest(RequestingFileInfo requestingFileInfo) {

    }

    @Override
    public void ackReceiveAndDownload(String fileHash) {

    }

    @Override
    public Boolean addBlock(Block block) {
        return null;
    }
}
