/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.all.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Simple {@link ServletContextListener} to test gh-2058.
 */
@Component
public class RegistrationServletContextListener implements ServletContextListener {

    @Autowired
    private NetworkMember me;

    @Value("${tracker.address}")
    private String trackerAddress;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //add our blocks to the blockchain ArrayList:

        System.out.println("*** contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String method = "/tracker/unregister";
        String uri = "http://" + trackerAddress + ":8080" + method;
        rt.exchange(uri, HttpMethod.POST, new HttpEntity<>(me), NetworkMember[].class);
    }

}
