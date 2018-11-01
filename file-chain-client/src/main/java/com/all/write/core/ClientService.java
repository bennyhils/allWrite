package com.all.write.core;

import com.all.write.NetworkMember;
import com.all.write.api.Block;
import com.all.write.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;

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
}
