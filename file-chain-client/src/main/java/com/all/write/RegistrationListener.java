package com.all.write;

import com.all.write.api.Block;
import com.all.write.core.ClientService;
import com.all.write.core.DataHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;

@Component
@DependsOn("clientExternalController")
public class RegistrationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationListener.class);

    @Value("${tracker.address}")
    private String trackerAddress;
    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private DataHolder dataHolder;

    @Autowired
    private ClientService clientService;

    @Autowired
    private NetworkMember networkMember;

    @PostConstruct
    public void init() {
        // w/a spring shit
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Map<String, NetworkMember> networkMemberMap = clientService.getNetworkMembersFromTracker(trackerAddress);
                dataHolder.setNetworkMembers(networkMemberMap);

                if (networkMemberMap.size() == 1) {
                    initBlockChain();
                } else {
                    dataHolder.setBlocks(clientService.sendPingExtAndGetChain(0));
                }
            } catch (Exception e) {
                System.out.println("err " + e);
                e.printStackTrace();
            }
        }).start();
    }

    private void initBlockChain() {
        Block genesisBlock = new Block();

        genesisBlock.setEncFileHash("0");
        genesisBlock.setFileHash("0");
        genesisBlock.setFileName("");
        genesisBlock.setFileSize(0L);

        genesisBlock.setPrevBlockHash("");
        genesisBlock.setSecretKey("");
        genesisBlock.setSender(clientService.getBase64EncodedPublicKey());
        genesisBlock.setType(Block.Type.GENESIS);

        clientService.signBlock(genesisBlock);

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(genesisBlock);
        dataHolder.setBlocks(blocks);

        LOGGER.info("Genesis block created");
    }
}