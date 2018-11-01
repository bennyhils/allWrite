package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.api.FileDto;
import com.all.write.api.FileStatus;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainInternal;
import com.all.write.api.rest.Response;
import com.all.write.core.ClientService;
import com.all.write.core.DataHolder;
import com.all.write.core.StateHolder;
import com.all.write.core.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    ClientService clientService;

    @Override
    @RequestMapping(value = "/member/list", method = RequestMethod.GET)
    @ResponseBody
    public Response list(String filter) {
        List<NetworkMember> data = new ArrayList<>(dataHolder.getAllNetworkMembers().values());
        Response response = new Response();
        response.getData().put("list", data);
        return response;
    }

    @Override
    public void upload(String fileLocalPath, NetworkMember targetExternalAddress) {
        NetworkMember target = dataHolder.getAllNetworkMembers().get(targetExternalAddress.getPublicKey());

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

        for (Map.Entry<String, Block> entry: map.entrySet()) {
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

        for (Map.Entry<String, Block> entry: map.entrySet()) {
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
            case SEND_KEY: fileDto.setFileStatus(FileStatus.SUCCESS); break;
            case SEND_FILE: fileDto.setFileStatus(FileStatus.IN_PROGRESS); break;
            case GET_FILE: fileDto.setFileStatus(FileStatus.TRANSFER); break;
            default: fileDto.setFileStatus(FileStatus.ERROR); break;
        }

        fileDto.setProgress(100.);
        fileDto.setSpeed(4096L);
        fileDto.setReceiver(new NetworkMember(Utils.getBase64Encoded(block.getReceiver()),
                block.getReceiverAddress()));
        fileDto.setSender(new NetworkMember(Utils.getBase64Encoded(block.getSender()),
                block.getSenderAddress()));

        return fileDto;
    }

    private Map<String, Block> getBlocksMap(List<Block> blocks) {
        Map<String, Block> map = new HashMap<>();

        for(Block block: blocks) {
            String key = block.getSender() + block.getFileHash();
            Block savedBlock = map.get(key);

            if (savedBlock == null || block.getType().getValue() > savedBlock.getType().getValue()) {
                map.put(key, block);
            }
        }

        return map;
    }
}
