package com.jay.instasearch.controller;

import com.jay.instasearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/search")
public class TestController {

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "searchService.getAllPost()";
    }
}
