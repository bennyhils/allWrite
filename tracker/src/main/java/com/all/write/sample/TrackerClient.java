package com.all.write.sample;

import com.all.write.NetworkMember;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class TrackerClient {

    public static void main(String[] args) {
        try {
            RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rt.getMessageConverters().add(new StringHttpMessageConverter());
            String uri = "http://localhost:8080/tracker/list";
            NetworkMember netMember = new NetworkMember("test-key", "localhost");
            NetworkMember[] returns = rt.postForObject(uri, netMember, NetworkMember[].class);

            System.out.println(Arrays.toString(returns));
        } catch(Exception e) {
            System.out.println("err " + e);
        }

    }
}
