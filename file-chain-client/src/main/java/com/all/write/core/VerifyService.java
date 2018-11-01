package com.all.write.core;

import com.all.write.api.Block;
import com.all.write.util.StringUtil;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;

@Component
public class VerifyService {

    public Boolean verifyAuthorSignature(Block block, String authorKey) {
        byte [] blockHash = StringUtil.getBlockBytes(block);
        PublicKey publicKey = StringUtil.getPublicKeyFromString(authorKey);

        if (publicKey == null) {
            return Boolean.FALSE;
        }

        try {
            StringUtil.verifyECDSASig(publicKey, blockHash, block.getAuthorSignature());
        } catch (Exception e) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Boolean verifyEncrypted(String hash, String encHash, String key) {
        try {
            Key secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] outputBytes = cipher.doFinal(Base64.getDecoder().decode(encHash);

            if (!Base64.getEncoder().encode(outputBytes).equals(hash)) {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
}
