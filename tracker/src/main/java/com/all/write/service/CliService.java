package com.all.write.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class CliService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliService.class);

    @Autowired
    private HelloWorldService service;

    @PostConstruct
    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        System.out.println("Enter your command...");

                        String command = reader.readLine();
                        if ("hello".equals(command)) {
                            System.out.println(service.getHelloMessage());
                        } else {
                            System.out.println("Unknown command: " + command);
                        }
                    } catch (IOException e) {
                        LOGGER.error("Error on handle command", e);
                    }
                }

            }
        }).start();
    }
}
