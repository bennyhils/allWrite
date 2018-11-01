package com.all.write.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/auth")
    @ResponseBody
    public boolean login(String key) {
        return true;
    }
}
