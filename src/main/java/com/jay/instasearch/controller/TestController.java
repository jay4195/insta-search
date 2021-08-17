package com.jay.instasearch.controller;


import com.jay.instasearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class TestController {
    @Autowired
    SearchService searchService;

    @RequestMapping(
            method = RequestMethod.GET)
    public String get() {
        return searchService.getAllPost();
    }
}
