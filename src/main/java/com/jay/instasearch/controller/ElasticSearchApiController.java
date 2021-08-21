package com.jay.instasearch.controller;




import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.InstaSearchService;
import com.jay.instasearch.service.ElasticSearchService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/search")
public class ElasticSearchApiController {
    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    InstaSearchService instaSearchService;

    @RequestMapping(value = "{query}", method = RequestMethod.GET)
    public List<Long> search(@PathVariable("query") String query) {
        return instaSearchService.search(query);
    }

    @RequestMapping(value = "createIndex",
                    method = RequestMethod.GET)
    public Object createIndex() {
        if (elasticSearchService.createElasticsearchIndex()) {
            return "Create Index Done!";
        } else {
            return "Create Index Failed!";
        }
    }

    @RequestMapping(value = "update",
                    method = RequestMethod.GET)
    public Object updateElasticSearch() {
        return elasticSearchService.updateElasticsearch();
    }


    @RequestMapping(value = "getAllPosts",
            method = RequestMethod.GET)
    public Object getAllPostInfo() {
        return elasticSearchService.getPostsFromDatabase();
    }

    @RequestMapping(value = "doSearch/{queryString}", method = RequestMethod.GET)
    public Object doSearch(@PathVariable String queryString) {
        return instaSearchService.doSearch(queryString);
    }

//    @RequestMapping(value = "getPostByUsername/{username}",
//            method = RequestMethod.GET)
//    public Object getPostByUsername(@PathVariable(value = "username") String username) {
//        return elasticSearchService.getPostsByUsername(username);
//    }
//
//    @RequestMapping(value = "getPostByCaption/{caption}",
//            method = RequestMethod.GET)
//    public Object getPostByCaption(@PathVariable(value = "caption") String caption) {
//        return elasticSearchService.getPostByCaption(caption);
//    }
//
//    @RequestMapping(value = "getPostByHashtags",
//            method = RequestMethod.POST)
//    public Object getPostsByHashtags(@RequestBody List<String> hashtags) {
//        return elasticSearchService.getPostsByHashtags(hashtags);
//    }

}
