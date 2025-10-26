package com.sds2.classes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageServer {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    // @GetMapping("/travel-info")
    // public String travelInfo() {
    //     return "travel-info";
    // }
    
}
