package com.jay.instasearch.controller;


import com.alibaba.fastjson.JSON;
import com.jay.instasearch.pojo.Post;
import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.PostSearchService;
import com.jay.instasearch.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.Index;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/search")
public class UpdatePostInfoController {
    @Autowired
    SearchService searchService;

    @Autowired
    RestHighLevelClient elasticsearchClient;

    @Autowired
    PostSearchService postSearchService;

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
        return postSearchService.getAllPosts();
    }

    @RequestMapping(value = "getPostByUsername/{username}",
            method = RequestMethod.GET)
    public Object getPostByUsername(@PathVariable(value = "username") String username) {
        return postSearchService.getPostsByUsername(username);
    }

    @RequestMapping(value = "getPostByCaption/{caption}",
            method = RequestMethod.GET)
    public Object getPostByCaption(@PathVariable(value = "caption") String caption) {
        return postSearchService.getPostByCaption(caption);
    }

    @RequestMapping(value = "getPostByHashtags",
            method = RequestMethod.POST)
    public Object getPostsByHashtags(@RequestBody List<String> hashtags) {
        return postSearchService.getPostsByHashtags(hashtags);
    }
}
