package com.all.write.web;

import com.all.write.api.Block;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainExternal;
import com.all.write.api.rest.Response;
import com.all.write.core.DataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller("clientExternalController")
public class ClientExternalController implements ChainExternal {

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
    public Response pingExt() {
        return null;
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/chain", method = RequestMethod.GET)
    public Response getChain() {
        Response response = new Response();
        response.getData().put("list", dataHolder.getBlocks());
        return response;
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
