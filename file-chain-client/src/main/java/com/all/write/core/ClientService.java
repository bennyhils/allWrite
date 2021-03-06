package com.all.write.core;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.api.LocalChainData;
import com.all.write.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ClientService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Autowired
    private NetworkMember me;

    @Autowired
    private DataHolder dataHolder;

    @Value("${tracker.address}")
    private String trackerAddress;

    public ClientService(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public void signBlock(Block block) {
        byte [] data = StringUtil.getBlockBytes(block);
        byte[] signature = StringUtil.applyECDSASig(privateKey, data);
        block.setAuthorSignature(signature);
    }

    public String getBase64EncodedPublicKey() {
        return StringUtil.getBase64Encoded(publicKey);
    }

    public Map<String, NetworkMember> getNetworkMembersFromTracker(String trackerAddress, boolean register) {
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String method = "/tracker/list";
        if (register) {
            method = "/tracker/register";
        }
        String uri = "http://" + trackerAddress + ":8080" + method;
        ResponseEntity<NetworkMember[]> response = rt.exchange(uri, HttpMethod.POST,
                new HttpEntity<>(me), NetworkMember[].class);
        List<NetworkMember> memberList = Arrays.asList(response.getBody());

        return memberList.stream()
                .collect(Collectors.toMap(NetworkMember::getPublicKey, i -> i,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private int sendBlockToChain(Block block) {
        dataHolder.setNetworkMembers(getNetworkMembersFromTracker(trackerAddress, false));
        int negativeCount = 0;
        int positiveCount = 0;

        for(NetworkMember networkMember: dataHolder.getAllNetworkMembers().values()) {
            RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rt.getMessageConverters().add(new StringHttpMessageConverter());
            String pingUri = "http://" + networkMember.getAddress() + "/ping";

            ResponseEntity<String> response = rt.exchange(pingUri, HttpMethod.GET,
                    null, String.class);

            if ("pong".equals(response.getBody())) {
                RestTemplate rt1 = new RestTemplate();
                rt1.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                rt1.getMessageConverters().add(new StringHttpMessageConverter());
                String addBlockUri = "http://" + networkMember.getAddress() + "/addBlock";

                ResponseEntity<Boolean> addBlockResp = rt.exchange(addBlockUri, HttpMethod.POST,
                        new HttpEntity<>(block), Boolean.class);

                if (addBlockResp.getBody()) {
                    positiveCount++;
                }  else {
                    negativeCount++;
                }
            }
        }

        if (positiveCount > negativeCount) {
            return positiveCount;
        } else {
            return -1 * positiveCount;
        }
    }

    public List<Block> sendPingExtAndGetChain(int positiveCount) {
        HashMap<String, NetworkMember> localChainDataMap = new HashMap<String, NetworkMember>();
        Map<String, Integer> countMap = new HashMap<>();

        for(NetworkMember networkMember: dataHolder.getAllNetworkMembers().values()) {
            if (networkMember.getAddress().equals(me.getAddress())) {
                continue;
            }
            RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rt.getMessageConverters().add(new StringHttpMessageConverter());
            String pingUri = "http://" + networkMember.getAddress() + "/pingExt";

            System.out.println(networkMember.getAddress() + " - address ping");

            ResponseEntity<LocalChainData> resp = rt.exchange(pingUri, HttpMethod.GET,
                    null, LocalChainData.class);
            String lastBlockHash = resp.getBody().getLastBlockHash();
            localChainDataMap.put(lastBlockHash, networkMember);

            Integer count = countMap.get(lastBlockHash);

            if (count == null) {
                count = 0;
            }

            count++;
            countMap.put(lastBlockHash, count);
        }

        int maxCount = 0;
        String keyOfMax = null;
        for (String key: countMap.keySet()) {
            Integer val = countMap.get(key);
            if (val > maxCount) {
                maxCount = val;
                keyOfMax = key;
            }
        }

        if (maxCount >= (-1 * positiveCount)) {
            NetworkMember maxMember = localChainDataMap.get(keyOfMax);
            return  obtainBlockChain(maxMember);
        }

        throw new RuntimeException("requested block chain is empty");
    }

    public void sendBlockChainAndProcessResult(Block block) {
        sendBlockChainAndProcessResult(block, 0);
    }

    private void sendBlockChainAndProcessResult(Block block, int invokeCount) {
        if (invokeCount > 5) {
            return;
        }

        int positiveCount = 0;
        if ((positiveCount = sendBlockToChain(block)) > 0) {
            dataHolder.addBlock(block);
        } else {
            List<Block> newChain = sendPingExtAndGetChain(positiveCount);
            List<Block> oldChain = dataHolder.getBlocks();
            List<Block> oldCopy = new ArrayList<>(oldChain);
            oldCopy.add(block);

            Iterator<Block> oldChainIt = oldChain.iterator();
            for (Block newBlock : newChain) {
                if (!oldChainIt.hasNext()) {
                    break;
                }
                Block oldBlock = oldChainIt.next();
                if (StringUtil.getHashOfBlock(newBlock).equals(StringUtil.getHashOfBlock(oldBlock))) {
                    oldCopy.remove(oldBlock);
                }
            }

            dataHolder.setBlocks(newChain);
            Block prevBlock = dataHolder.lastBlock();
            for (Block myBlock : oldCopy) {
                myBlock.setPrevBlockHash(StringUtil.getHashOfBlock(prevBlock));
                prevBlock = myBlock;
                sendBlockChainAndProcessResult(myBlock, ++invokeCount);
            }
        }
    }

    private List<Block> obtainBlockChain(NetworkMember member) {
        // choose true chain holder
        String address = member.getAddress();

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://" + address + "/chain";
        ResponseEntity<Block[]> response = rt.exchange(uri,  HttpMethod.GET, null, Block[].class);
        return Arrays.asList(response.getBody());
    }
}
