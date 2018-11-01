package com.all.write.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class UpdateService {

    @Autowired
    private DataHolder dataHolder;
    @Autowired
    private ClientService clientService;
    @Value("${tracker.address}")
    private String trackerAddress;

    @PostConstruct
    public void init() {
        //todo create true update
        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(4);
        scheduledPool.scheduleWithFixedDelay(() -> {
            if (!dataHolder.getBlocks().isEmpty()) {
                dataHolder.setNetworkMembers(clientService.getNetworkMembersFromTracker(trackerAddress));
            }
        }, 10, 1, TimeUnit.SECONDS);
    }
}
