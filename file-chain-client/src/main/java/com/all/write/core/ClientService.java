package com.all.write.core;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClientService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Autowired
    private NetworkMember networkMember;

    public ClientService(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public void signBlock(Block block) {
        byte [] data = StringUtil.getBlockBytes(block);
        byte[] signature = StringUtil.applyECDSASig(privateKey, data);
        block.setAuthorSignature(signature);
    }

    /**
     * check if block sender is author
     */
    public boolean verifySignature(Block block, PublicKey sender) {
        byte [] data = StringUtil.getBlockBytes(block);
        return StringUtil.verifyECDSASig(sender, data, block.getAuthorSignature());
    }

    public String getBase64EncodedPublicKey() {
        return StringUtil.getBase64Encoded(publicKey);
    }

    public Map<String, NetworkMember> getNetworkMembersFromTracker(String trackerAddress) {
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://" + trackerAddress + ":8080/tracker/list";
        ResponseEntity<NetworkMember[]> response = rt.exchange(uri, HttpMethod.POST,
                new HttpEntity<>(networkMember), NetworkMember[].class);
        List<NetworkMember> memberList = Arrays.asList(response.getBody());

        Map<String, NetworkMember> networkMemberMap = memberList.stream()
                .collect(Collectors.toMap(NetworkMember::getPublicKey, i -> i));

        return networkMemberMap;
    }
}
