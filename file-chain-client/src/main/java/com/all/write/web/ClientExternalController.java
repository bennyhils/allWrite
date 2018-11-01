package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainExternal;
import com.all.write.core.DataHolder;
import com.all.write.core.StateHolder;
import com.all.write.core.VerifyService;
import com.all.write.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Map;

@RestController("clientExternalController")
public class ClientExternalController implements ChainExternal {

    @Autowired
    private StateHolder stateHolder;

    @Autowired
    private DataHolder dataHolder;

    @Autowired
    private VerifyService verifyService;

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
    @GetMapping(value = "/acceptUploadRequest", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] acceptUploadRequest(String fileHash) {
        RequestingFileInfo outgoingInfo = stateHolder.getOutgoingRequest(fileHash);


        File file2Upload = new File(outgoingInfo.getOriginFilePath());
        System.out.println("The length of the file is : " + file2Upload.length());

        try {
            return Files.readAllBytes(file2Upload.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/requestKey", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Override
    @ResponseBody
    public byte[] requestKey(String fileHash) {
        return stateHolder.getSecretKey(fileHash);
    }

    @Override
    @PostMapping("/addBlock")
    public Boolean addBlock(@RequestBody Block block, HttpServletRequest request) {
        if (block == null) {
            return Boolean.FALSE;
        }
        request.getRequestURI();

        //FIXME: verify all fields
        if (block.getAuthorSignature() == null
                || block.getReceiver() == null
                || block.getSender() == null
                || block.getAuthorSignature() == null) {
            return Boolean.FALSE;
        }

        Map<String, NetworkMember> membersMap = dataHolder.getAllNetworkMembers();
        String authorKey = StringUtil.getAuthorPublicKey(block);

        if (authorKey == null) {
            return Boolean.FALSE;
        }

        if (membersMap.containsKey(authorKey)) {
            if (!verifyService.verifyAuthorSignature(block, authorKey)) {
                return Boolean.FALSE;
            }
        } else {
            //FIXME: go to tracker
            return Boolean.FALSE;
        }

        String lastHash = dataHolder.getBlocks().isEmpty() ? null :
                StringUtil.getHashOfBlock((Block)((LinkedList) dataHolder.getBlocks()).getLast());

        if (lastHash == null ||  block.getPrevBlockHash().equals(lastHash)) {
            if (block.getType() == Block.Type.SEND_KEY) {
                String key = block.getSecretKey();

                if (key == null) {
                    return Boolean.FALSE;
                }

                if (!verifyService.verifyEncrypted(block.getFileHash(), block.getEncFileHash(), key)) {
                    return Boolean.FALSE;
                }
            }

            dataHolder.getBlocks().add(block);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


}
