package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainExternal;
import com.all.write.core.ClientService;
import com.all.write.core.DataHolder;
import com.all.write.core.StateHolder;
import com.all.write.core.VerifyService;
import com.all.write.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@RestController("clientExternalController")
public class ClientExternalController implements ChainExternal {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientExternalController.class);

    @Autowired
    private StateHolder stateHolder;

    @Autowired
    private DataHolder dataHolder;

    @Autowired
    private VerifyService verifyService;

    @Value("${tracker.address}")
    private String trackerAddress;

    @Autowired
    private ClientService clientService;

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

    @GetMapping("/pingExt")
    @ResponseBody
    @Override
    public ResponseEntity pingExt() {
        LocalChainData chain = new LocalChainData();
        chain.setChainLength((long) dataHolder.getBlocks().size());
        chain.setLastBlockHash(StringUtil.getHashOfBlock(dataHolder.lastBlock()));
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
    @PostMapping(value = "/acceptUploadRequest", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] acceptUploadRequest(@RequestBody String fileHash) {
        RequestingFileInfo outgoingInfo = stateHolder.getOutgoingRequest(fileHash);

        File file2Upload = new File(outgoingInfo.getOriginFilePath());
        System.out.println("The length of the file is : " + file2Upload.length());

        try {
            byte[] secretKeyBytes = stateHolder.getSecretKey(fileHash);
            Key secretKey = new SecretKeySpec(Base64.getEncoder().encode(secretKeyBytes), "AES");
            Cipher cipher = null;
            byte[] bytes = Files.readAllBytes(file2Upload.toPath());
            byte[] outputBytes;
            try (ByteArrayInputStream fos = new ByteArrayInputStream(bytes)) {
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                outputBytes = cipher.doFinal(bytes);

                return outputBytes;
            } catch (IOException | NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkMember(String authorKey, Block block) throws Exception {
        Map<String, NetworkMember> membersMap = dataHolder.getAllNetworkMembers();

        if (membersMap.containsKey(authorKey)) {
            if (!verifyService.verifyAuthorSignature(block, authorKey)) {
                throw new Exception("VerifyAuthorSignature failed!");
            }

            return true;
        }

        return false;
    }

    @PostMapping(value = "/requestKey", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Override
    @ResponseBody
    public byte[] requestKey(@RequestBody String fileHash) {
        return stateHolder.getSecretKey(fileHash);
    }

    @Override
    @PostMapping("/addBlock")
    public Boolean addBlock(@RequestBody Block block) {
        if (block == null) {
            return Boolean.FALSE;
        }

        if (block.getAuthorSignature() == null
                || block.getReceiver() == null
                || block.getSender() == null
                || block.getAuthorSignature() == null) {
            LOGGER.warn("block is partially empty {}.", block);
            return Boolean.FALSE;
        }

        String authorKey = StringUtil.getAuthorPublicKey(block);

        if (authorKey == null) {
            LOGGER.warn("block author key is null {}.", block);
            return Boolean.FALSE;
        }

        try {
            if (!checkMember(authorKey, block)) {
                dataHolder.setNetworkMembers(clientService.getNetworkMembersFromTracker(trackerAddress));

                if (!checkMember(authorKey, block)) {
                    LOGGER.warn("block author key verification failed {}.", block);
                    return Boolean.FALSE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn("block secret key verification failed {}.", block, e);
            return Boolean.FALSE;
        }

        String lastHash = StringUtil.getHashOfBlock(dataHolder.lastBlock());

        if (block.getPrevBlockHash().equals(lastHash)) {
            if (block.getType() == Block.Type.SEND_KEY) {
                String key = block.getSecretKey();

                if (key == null) {
                    LOGGER.warn("block secret key verification failed. Secret key is null <{}>", block);
                    return Boolean.FALSE;
                }

                if (!verifyService.verifyEncrypted(block.getFileHash(), block.getEncFileHash(), key)) {
                    LOGGER.warn("block secret key verification failed. Secret key is invalid <{}>", block);
                    return Boolean.FALSE;
                }
            }

            dataHolder.getBlocks().add(block);


            LOGGER.info("block successfully added <{}>, chain size <{}>", block, dataHolder.getBlocks().size());
            return Boolean.TRUE;
        }

        LOGGER.warn("block hash verification fail <{}>", block);
        return Boolean.FALSE;
    }


}
