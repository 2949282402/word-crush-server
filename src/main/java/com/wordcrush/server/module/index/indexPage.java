package com.wordcrush.server.module.index;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class indexPage {

    @RequestMapping("/")
    public String index() {
        return "Welcome";
    }
}
