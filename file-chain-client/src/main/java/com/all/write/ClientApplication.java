package com.all.write;

import com.all.write.core.ClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

@SpringBootApplication
public class ClientApplication {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    public static void main(String[] args) {
        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

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

        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    public NetworkMember networkMember(@Value("${address}") String address){
        return new NetworkMember(publicKey.toString(), address);
    }

    @Bean
    public ClientService clientService() {
        return new ClientService(privateKey, publicKey);
    }
}
