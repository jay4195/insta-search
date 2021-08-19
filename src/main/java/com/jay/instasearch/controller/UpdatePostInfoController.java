package com.jay.instasearch.controller;



import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.InstaSearchService;
import com.jay.instasearch.service.ElasticSearchService;
import com.jay.instasearch.service.RemoteSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/search")
public class UpdatePostInfoController {
    @Autowired
    RemoteSearchService searchService;

    @Autowired
    RestHighLevelClient elasticsearchClient;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    InstaSearchService instaSearchService;

    @RequestMapping(value = "createIndex",
                    method = RequestMethod.GET)
    public Object createIndex() throws IOException {
        if (elasticSearchService.createIndex()) {
            return "Create Index Done!";
        } else {
            return "Create Index Failed!";
        }
    }

    @RequestMapping(value = "testUpdate",
                    method = RequestMethod.GET)
    public Object testUpdate() {
        return elasticSearchService.updatePostInfo();
    }

    @RequestMapping(
            method = RequestMethod.GET)
    public Object updatePostInfo() throws IOException {
        List<SearchSchema> postInfoList = searchService.getAllPost();
        for (SearchSchema searchSchema : postInfoList) {
            System.out.println(searchSchema);
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("postId", searchSchema.getPostId());
            jsonMap.put("username", searchSchema.getUsername());
            jsonMap.put("caption", searchSchema.getCaption());
            jsonMap.put("hashtags", searchSchema.getHashtags());
            IndexRequest indexRequest = new IndexRequest("post-info").id(searchSchema.getPostId().toString()).source(jsonMap);
            elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        }
        return searchService.getAllPost();
    }

    @RequestMapping(value = "getAllPostInfo",
            method = RequestMethod.GET)
    public Object getAllPostInfo() {
        return elasticSearchService.getAllPosts();
    }

    @RequestMapping(value = "getPostByUsername/{username}",
            method = RequestMethod.GET)
    public Object getPostByUsername(@PathVariable(value = "username") String username) {
        return elasticSearchService.getPostsByUsername(username);
    }

    @RequestMapping(value = "getPostByCaption/{caption}",
            method = RequestMethod.GET)
    public Object getPostByCaption(@PathVariable(value = "caption") String caption) {
        return elasticSearchService.getPostByCaption(caption);
    }

    @RequestMapping(value = "getPostByHashtags",
            method = RequestMethod.POST)
    public Object getPostsByHashtags(@RequestBody List<String> hashtags) {
        return elasticSearchService.getPostsByHashtags(hashtags);
    }

    @RequestMapping(value = "doSearch/{queryString}", method = RequestMethod.GET)
    public Object getPostsByHashtags(@PathVariable String queryString) {
        return instaSearchService.doSearch(queryString);
    }
}
