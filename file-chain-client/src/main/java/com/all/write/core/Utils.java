package com.all.write.core;

import com.all.write.api.Block;

import java.security.PublicKey;
import java.util.Base64;

public class Utils {
    public static String getBase64Encoded(PublicKey publicKey) {
        if (publicKey != null) {
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        }

        return null;
    }
    //@FIXME: to do
    public static String getHashOfBlock(Block block) {
        return "DEADBEAF";
    }
}
