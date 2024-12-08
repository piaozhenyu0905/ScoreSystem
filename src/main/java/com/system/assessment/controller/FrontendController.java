package com.system.assessment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    // 捕获所有请求，除了静态资源请求，返回 index.html
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirectToFrontend() {
        return "forward:/index.html"; // 直接返回 index.html
    }
}