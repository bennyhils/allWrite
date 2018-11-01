package com.all.write.core;

import java.security.PublicKey;
import java.util.Base64;

public class Utils {
    public static String getBase64Encoded(PublicKey publicKey) {
        if (publicKey != null) {
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        }

        return null;
    }
}
