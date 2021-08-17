package com.jay.instasearch.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "instagram-search-provider")
public interface SearchService {
//    List<SearchSchema> getAllPost();

    @RequestMapping(value = "/search-schema/get-all-post",
            method = RequestMethod.GET)
    String getAllPost();
}
