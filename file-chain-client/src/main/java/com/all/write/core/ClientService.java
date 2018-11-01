package com.all.write.core;

import com.all.write.StringUtil;
import com.all.write.api.Block;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

@Component
public class ClientService {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void signBlock(Block block) {
        String data = block.toString();
        byte[] signature = StringUtil.applyECDSASig(privateKey, data);
        block.setAuthorSignature(signature);
    }

    /**
     * check if block sender is author
     */
    public boolean verifySignature(Block block, PublicKey sender) {
        String data = block.toString();
        return StringUtil.verifyECDSASig(sender, data, block.getAuthorSignature());
    }

    public PublicKey publicKey() {
        return publicKey;
    }
}
