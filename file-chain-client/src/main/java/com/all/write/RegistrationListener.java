package com.all.write;

import com.all.write.core.DataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
@DependsOn("clientExternalController")
public class RegistrationListener {

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
                String uri = "http://localhost:8080/tracker/list";
                NetworkMember netMember = new NetworkMember("test-key", "localhost:8090");
                NetworkMember[] returns = rt.postForObject(uri, netMember, NetworkMember[].class);

                assert returns != null;
                dataHolder.setNetworkMembers(Arrays.asList(returns));
            } catch (Exception e) {
                System.out.println("err " + e);
            }
        }).start();
    }
}