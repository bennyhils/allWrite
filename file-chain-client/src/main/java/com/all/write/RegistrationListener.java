package com.all.write;

import com.all.write.api.Block;
import com.all.write.core.ClientService;
import com.all.write.core.DataHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@DependsOn("clientExternalController")
public class RegistrationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationListener.class);

    @Value("${local.address}")
    private String localAddress;

    @Autowired
    private DataHolder dataHolder;

    @Autowired
    private ClientService clientService;

    @PostConstruct
    public void init() {
        // w/a spring shit
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                RestTemplate rt = new RestTemplate();
                rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                rt.getMessageConverters().add(new StringHttpMessageConverter());
                String uri = "http://" + localAddress + ":8080/tracker/list";
                NetworkMember netMember = new NetworkMember("test-key", "localhost:8090");
                ResponseEntity<NetworkMember[]> response = rt.exchange(uri, HttpMethod.POST,
                        new HttpEntity<>(netMember), NetworkMember[].class);
                List<NetworkMember> memberList = Arrays.asList(response.getBody());


                Map<String, NetworkMember> networkMemberMap = memberList.stream()
                        .collect(Collectors.toMap(NetworkMember::getPublicKey, i -> i));
                dataHolder.setNetworkMembers(networkMemberMap);

                if (memberList.size() == 1) {
                    initBlockChain();
                } else {
                    getBlockChain(memberList);
                }
            } catch (Exception e) {
                System.out.println("err " + e);
            }
        }).start();
    }

    private void getBlockChain(List<NetworkMember> memberArray) {

        // choose true chain holder
        String address = memberArray.get(0).getAddress();

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://" + address + ":8080/chain";
        ResponseEntity<Block[]> response = rt.exchange(uri,  HttpMethod.GET, null, Block[].class);
        List<Block> chain = Arrays.asList(response.getBody());

        assert chain.size() > 0;
        dataHolder.setBlocks(chain);

    }

    private void initBlockChain() {
        ArrayList<Block> blocks = new ArrayList<>();
        Block genesisBlock = new Block();

        genesisBlock.setEncFileHash("0");
        genesisBlock.setFileHash("0");
        genesisBlock.setFileName("");
        genesisBlock.setFileSize(0L);

        genesisBlock.setPrivBlockHash("");
        genesisBlock.setSecretKey("");
        genesisBlock.setSender(clientService.publicKey());
        genesisBlock.setType(Block.Type.GENESIS);

        clientService.signBlock(genesisBlock);

        dataHolder.setBlocks(blocks);

        LOGGER.info("Genesis block created");
    }
}