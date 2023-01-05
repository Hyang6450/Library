package com.korit.library.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping({"", "/index"}) // 두가지 요청 주소를 다 사용가능하다.
    public String index() {
        return "index";
    }
}
