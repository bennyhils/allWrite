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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
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
    public void uploadRequest(String fileLocalPath, NetworkMember targetNetworkMember) {

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        RequestingFileInfo fileInfo = RequestingFileInfo.createFileInfo(fileLocalPath, me);
        String uri = "http://" + targetNetworkMember.getAddress() + "/receiveFileRequest";
        rt.postForObject(uri,  fileInfo, RequestingFileInfo.class);

        stateHolder.addOutgoingFiles(fileInfo);

        createSendFileRequest(fileInfo);

    }

    private void createSendFileRequest(RequestingFileInfo fileInfo) {
        Block block = new Block();
        block.setType(Block.Type.SEND_FILE);
        block.setSender(me.getPublicKey());

        block.setSecretKey("");

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
    public void download(RequestingFileInfo fileInfo, String localFilePath) {

    }

    @Override
    public List<FileDto> getOutgoingFiles() {
        List<FileDto> ret = new ArrayList<>();
        List<Block> blocks = dataHolder.getBlocks();
        Map<String, Block> map = getBlocksMap(blocks);

        String selfPublicKey = clientService.getBase64EncodedPublicKey();

        for (Map.Entry<String, Block> entry : map.entrySet()) {
            Block block = entry.getValue();

            if (entry.getKey().startsWith(selfPublicKey)) {
                FileDto fileDto = genFileDto(block, selfPublicKey);
                ret.add(fileDto);
            }
        }

        return ret;
    }

    @Override
    public List<FileDto> getIncomingFiles() {
        List<FileDto> ret = new ArrayList<>();
        List<Block> blocks = dataHolder.getBlocks();
        Map<String, Block> map = getBlocksMap(blocks);
        String selfPublicKey = clientService.getBase64EncodedPublicKey();

        for (Map.Entry<String, Block> entry : map.entrySet()) {
            Block block = entry.getValue();

            if (!entry.getKey().startsWith(selfPublicKey)) {
                FileDto fileDto = genFileDto(block, selfPublicKey);
                ret.add(fileDto);
            }
        }

        return ret;
    }

    private FileDto genFileDto(Block block, String selfPublicKey) {
        FileDto fileDto = new FileDto();
        fileDto.setId(selfPublicKey);
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
