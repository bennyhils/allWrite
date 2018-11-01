package com.all.write;

import com.all.write.core.DataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@DependsOn("clientExternalController")
public class RegistrationListener {

    @Value("${local.address}")
    private String localAddress;

    @Autowired
    private DataHolder dataHolder;

    @PostConstruct
    public void init() {
        // w/a spring shit
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                RestTemplate rt = new RestTemplate();
                rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                rt.getMessageConverters().add(new StringHttpMessageConverter());
                String uri = "http://" + localAddress + ":8080/tracker/list";
                NetworkMember netMember = new NetworkMember("test-key", "localhost:8090");
                NetworkMember[] returns = rt.postForObject(uri, netMember, NetworkMember[].class);

                assert returns != null;
                Map<String, NetworkMember> networkMemberMap = Arrays.stream(returns).collect(Collectors.toMap(NetworkMember::getPublicKey, i -> i));
                dataHolder.setNetworkMembers(networkMemberMap);
            } catch (Exception e) {
                System.out.println("err " + e);
            }
        }).start();
    }
}