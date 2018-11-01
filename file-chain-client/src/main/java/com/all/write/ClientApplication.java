package com.all.write;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    public NetworkMember networkMember(@Value("${public.key}") String publicKey, @Value("${address}") String address){
        return new NetworkMember(publicKey, address);
    }
}
