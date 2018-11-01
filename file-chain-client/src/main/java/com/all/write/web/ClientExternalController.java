package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainExternal;
import com.all.write.core.DataHolder;
import com.all.write.core.StateHolder;
import com.all.write.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Map;

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

    @Override
    public Boolean addBlock(Block block) {
        if (block == null) {
            return Boolean.FALSE;
        }

        if (block.getAuthorSignature() == null) {
            return Boolean.FALSE;
        }

        Map<String, NetworkMember> membersMap = dataHolder.getAllNetworkMembers();
        String authorKey = StringUtil.getAuthorPublicKey(block);

        if (authorKey == null) {
            return Boolean.FALSE;
        }

        if (membersMap.containsKey(authorKey)) {
            if (!verifyAuthorSignature(block, authorKey)) {
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

                try {
                    Key secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    byte[] outputBytes = cipher.doFinal(Base64.getDecoder().decode(block.getEncFileHash()));

                    if (!Base64.getEncoder().encode(outputBytes).equals(block.getFileHash())) {
                        return Boolean.FALSE;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Boolean.FALSE;
                }

            }

            dataHolder.getBlocks().add(block);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private boolean verifyAuthorSignature(Block block, String authorKey) {
        byte [] blockHash = StringUtil.getBlockBytes(block);
        byte [] keyBytes = Base64.getDecoder().decode(authorKey);

        PublicKey publicKey = null;

        try {
            publicKey =
                    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (publicKey == null) {
            return Boolean.FALSE;
        }

        try {
            StringUtil.verifyECDSASig(publicKey, blockHash, block.getAuthorSignature());
        } catch (Exception e) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
