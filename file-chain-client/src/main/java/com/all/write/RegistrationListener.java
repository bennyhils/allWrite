package com.all.write;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class RegistrationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rt.getMessageConverters().add(new StringHttpMessageConverter());
            String uri = "http://localhost:8080/tracker/list";
            NetworkMember netMember = new NetworkMember("test-key", "localhost:8090");
            NetworkMember[] returns = rt.postForObject(uri, netMember, NetworkMember[].class);

            System.out.println(Arrays.toString(returns));
        } catch (Exception e) {
            System.out.println("err " + e);
        }
    }
}