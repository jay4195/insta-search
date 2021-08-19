package com.jay.instasearch.service;

import com.jay.instasearch.pojo.SearchSchema;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * OpenFeignClient 远程调用接口
 * @author jay4195
 */
@FeignClient(name = "instagram-search-provider")
public interface RemoteSearchService {

    @RequestMapping(value = "/search-schema/get-all-post",
            method = RequestMethod.GET)
    List<SearchSchema> getAllPost();

}
