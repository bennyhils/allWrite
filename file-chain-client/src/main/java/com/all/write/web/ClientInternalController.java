package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.api.FileDto;
import com.all.write.api.FileStatus;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainInternal;
import com.all.write.core.ClientService;
import com.all.write.core.DataHolder;
import com.all.write.core.StateHolder;
import com.all.write.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;


@RestController
public class ClientInternalController implements ChainInternal {

    @Autowired
    private DataHolder dataHolder;

    @Autowired
    private StateHolder stateHolder;

    @Autowired
    private ClientService clientService;

    @Autowired
    private NetworkMember me;

    @Override
    @RequestMapping(value = "/member/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity list(String filter) {
        List<NetworkMember> data = new ArrayList<>(dataHolder.getAllNetworkMembers().values());
        return new ResponseEntity<>(data.toArray(), HttpStatus.OK);
    }

    @Override
    @PostMapping("/uploadRequest")
    public void uploadRequest(String fileLocalPath, @RequestBody NetworkMember targetNetworkMember) {

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        byte[] secretKeyBytes = null;
        SecretKey secretKey = null;

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom(); // cryptograph. secure random
            keyGen.init(random);
            secretKey = keyGen.generateKey();
            secretKeyBytes = secretKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        RequestingFileInfo fileInfo = RequestingFileInfo.createFileInfo(fileLocalPath, me, secretKey);
        String uri = "http://" + targetNetworkMember.getAddress() + "/receiveFileRequest";
        rt.postForObject(uri, fileInfo, RequestingFileInfo.class);

        stateHolder.addOutgoingFiles(fileInfo);
        stateHolder.addFileSecretKey(fileInfo.getHash(), secretKeyBytes);


        createSendFileRequest(fileInfo);

    }

    private void createSendFileRequest(RequestingFileInfo fileInfo) {
        Block block = new Block();
        block.setType(Block.Type.SEND_FILE);
        block.setSender(me.getPublicKey());
        block.setPrevBlockHash(StringUtil.getHashOfBlock(dataHolder.lastBlock()));
        block.setFileSize(fileInfo.getFileSize());
        block.setFileName(fileInfo.getOriginFilePath());
        block.setFileHash(fileInfo.getHash());
        block.setEncFileHash(fileInfo.getEncFileHash());
    }

    @Override
    @RequestMapping(value = "/requests/list", method = RequestMethod.GET)
    public List<RequestingFileInfo> listRequests() {
        return stateHolder.getRequestingFileInfos();
    }

    @Override
    @PostMapping(value = "/download")
    public void download(String localFilePath, @RequestBody RequestingFileInfo fileInfo) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate(messageConverters);
        String url = "http://" + fileInfo.getSender().getAddress();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/octet-stream");


        HttpEntity<String> entity = new HttpEntity<>(fileInfo.getHash(), headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url + "/acceptUploadRequest",
                HttpMethod.POST, entity, byte[].class);


        writeFileReceivedBlock(fileInfo);

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url + "/requestKey", HttpMethod.POST, entity, byte[].class,
                fileInfo.getHash());
        byte[] secretKeyBytes = responseEntity.getBody();
        Key secretKey = new SecretKeySpec(Base64.getEncoder().encode(secretKeyBytes), "AES");
        Cipher cipher;
        byte[] outputBytes;
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            outputBytes = cipher.doFinal(response.getBody());
            fos.write(outputBytes);
        } catch (IOException | NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        writeFileSuccesfullyLoadedBlock(fileInfo, secretKeyBytes);
    }

    private void writeFileSuccesfullyLoadedBlock(RequestingFileInfo fileInfo, byte[] secretKeyBytes) {
        Block block = new Block();
        block.setFileHash(fileInfo.getHash());
        block.setFileName(fileInfo.getOriginFilePath());
        block.setFileSize(fileInfo.getFileSize());
        block.setEncFileHash(fileInfo.getEncFileHash());

        block.setSecretKey(Base64.getEncoder().encodeToString(secretKeyBytes));
        block.setSender(fileInfo.getSender().getPublicKey());
        block.setType(Block.Type.SEND_KEY);
        block.setReceiver(me.getPublicKey());
        block.setReceiverAddress(me.getAddress());
        block.setSenderAddress(fileInfo.getSender().getAddress());
        block.setPrevBlockHash(StringUtil.getHashOfBlock(dataHolder.lastBlock()));
        clientService.signBlock(block);

        clientService.sendBlockChainAndProcessResult(block);
    }

    private void writeFileReceivedBlock(RequestingFileInfo fileInfo) {
        Block fileReceivedBlock = new Block();
        fileReceivedBlock.setFileHash(fileInfo.getHash());
        fileReceivedBlock.setFileName(fileInfo.getOriginFilePath());
        fileReceivedBlock.setFileSize(fileInfo.getFileSize());
        fileReceivedBlock.setEncFileHash(fileInfo.getEncFileHash());

        fileReceivedBlock.setSecretKey("");
        fileReceivedBlock.setSender(fileInfo.getSender().getPublicKey());
        fileReceivedBlock.setType(Block.Type.GET_FILE);
        fileReceivedBlock.setReceiver(me.getPublicKey());
        fileReceivedBlock.setReceiverAddress(me.getAddress());
        fileReceivedBlock.setSenderAddress(fileInfo.getSender().getAddress());
        fileReceivedBlock.setPrevBlockHash(StringUtil.getHashOfBlock(dataHolder.lastBlock()));
        clientService.signBlock(fileReceivedBlock);
        clientService.sendBlockChainAndProcessResult(fileReceivedBlock);

    }

    private List<FileDto> getOutgoingFilesHistory() {
        List<FileDto> ret = new ArrayList<>();
        List<Block> blocks = dataHolder.getBlocks();
        Map<String, Block> map = getBlocksMap(blocks);

        String selfPublicKey = clientService.getBase64EncodedPublicKey();

        for (Map.Entry<String, Block> entry : map.entrySet()) {
            Block block = entry.getValue();

            if (entry.getKey().startsWith(selfPublicKey)) {
                FileDto fileDto = genFileDto(block);
                ret.add(fileDto);
            }
        }

        return ret;
    }

    @Override
    @RequestMapping(value = "/outgoing/list", method = RequestMethod.GET)
    @ResponseBody
    public List<FileDto> getOutgoingFiles() {
        List<FileDto> ret = new ArrayList<>();
        Collection<RequestingFileInfo> outgoingRequests = stateHolder.getOutgoingRequests();

        for(RequestingFileInfo requestingFileInfo: outgoingRequests) {
            ret.add(convertRequestToFileDto(requestingFileInfo));
        }

        ret.addAll(getOutgoingFilesHistory());

        return ret;
    }

    private List<FileDto> getIncomingFilesHistory() {
        // downloads history
        List<FileDto> ret = new ArrayList<>();
        List<Block> blocks = dataHolder.getBlocks();
        Map<String, Block> map = getBlocksMap(blocks);
        String selfPublicKey = clientService.getBase64EncodedPublicKey();

        for (Map.Entry<String, Block> entry : map.entrySet()) {
            Block block = entry.getValue();

            if (!entry.getKey().startsWith(selfPublicKey)) {
                FileDto fileDto = genFileDto(block);
                ret.add(fileDto);
            }
        }



        return ret;
    }

    @Override
    @RequestMapping(value = "/incoming/list", method = RequestMethod.GET)
    @ResponseBody
    public List<FileDto> getIncomingFiles() {
        // active downloads
        List<FileDto> ret = new ArrayList<>();

        List<RequestingFileInfo> requestingFileInfoList = stateHolder.getRequestingFileInfos();
        for (RequestingFileInfo requestingFileInfo : requestingFileInfoList) {
            ret.add(convertRequestToFileDto(requestingFileInfo));
        }

        ret.addAll(getIncomingFilesHistory());

        return ret;
    }

    private FileDto convertRequestToFileDto(RequestingFileInfo requestingFileInfo) {
        FileDto fileDto = new FileDto();
        fileDto.setSize(requestingFileInfo.getFileSize());
        fileDto.setSender(requestingFileInfo.getSender());
        fileDto.setReceiver(me);
        fileDto.setId(requestingFileInfo.getHash());
        fileDto.setFileStatus(FileStatus.TRANSFER);
        fileDto.setProgress(0.);
        //todo to nayob or not to nayob, vot v chem vopros
        fileDto.setSpeed(new Random().nextLong() % (requestingFileInfo.getFileSize() / 10));
        return fileDto;
    }

    private FileDto genFileDto(Block block) {
        FileDto fileDto = new FileDto();
        fileDto.setId(block.getFileHash());
        fileDto.setName(block.getFileName());

        switch (block.getType()) {
            case SEND_KEY:
                fileDto.setFileStatus(FileStatus.SUCCESS);
                break;
            case SEND_FILE:
                fileDto.setFileStatus(FileStatus.IN_PROGRESS);
                break;
            case GET_FILE:
                fileDto.setFileStatus(FileStatus.TRANSFER);
                break;
            default:
                fileDto.setFileStatus(FileStatus.ERROR);
                break;
        }

        fileDto.setProgress(100.);
        fileDto.setSpeed(4096L);
        fileDto.setReceiver(new NetworkMember(block.getReceiver(),
                block.getReceiverAddress()));
        fileDto.setSender(new NetworkMember(block.getSender(),
                block.getSenderAddress()));

        return fileDto;
    }

    private Map<String, Block> getBlocksMap(List<Block> blocks) {
        Map<String, Block> map = new HashMap<>();

        for (Block block : blocks) {
            String key = block.getSender() + block.getFileHash();
            Block savedBlock = map.get(key);

            if (savedBlock == null || block.getType().getValue() > savedBlock.getType().getValue()) {
                map.put(key, block);
            }
        }

        return map;
    }
}
