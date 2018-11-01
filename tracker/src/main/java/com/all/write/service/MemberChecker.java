package com.all.write.service;

import com.all.write.NetworkMember;
import com.all.write.web.NetworkMemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MemberChecker {

    @Autowired
    private NetworkMemberDao networkMemberDao;

    @Value("${ping.timeout}")
    private Long pingTimeout;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplate = restTemplateBuilder
                .setConnectTimeout(5000)
                .build();


        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(4);
        scheduledPool.scheduleWithFixedDelay(() -> {

            List<NetworkMember> members = networkMemberDao.list();
            members.forEach(networkMember -> {
                if (!pingMember(networkMember)) {
                    networkMemberDao.remove(networkMember);
                }
            });
        }, pingTimeout, 1, TimeUnit.SECONDS);
    }

    public boolean pingMember(NetworkMember member){
        String pingUrl = "http://" + member.getAddress() + "/ping";
        String result = restTemplate.getForObject(pingUrl, String.class);

        return true;
    }
}
