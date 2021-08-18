package com.jay.instasearch.controller;



import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.ElasticSearchService;
import com.jay.instasearch.service.PostSearchService;
import com.jay.instasearch.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
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

    @Autowired
    ElasticSearchService elasticSearchService;

    @RequestMapping(value = "createIndex",
                    method = RequestMethod.GET)
    public Object createIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("post-info");
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        boolean exists = elasticsearchClient.indices().exists(request, RequestOptions.DEFAULT);
        if (exists) {
            System.out.println("index-exist");
            return "index-exist";
        } else {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("post-info");
//            createIndexRequest.settings(Settings.builder()
//                    .put("index.number_of_shards", 3)
//                    .put("index.number_of_replicas", 2)
//            );
            Map<String, Object> caption = new HashMap<>();
            caption.put("type", "text");
            caption.put("analyzer", "ik_max_word");
            Map<String, Object> hashtags = new HashMap<>();
            hashtags.put("type", "text");
            hashtags.put("analyzer", "ik_max_word");
            Map<String, Object> postId = new HashMap<>();
            postId.put("type", "long");
            Map<String, Object> username = new HashMap<>();
            username.put("type", "text");

            Map<String, Object> properties = new HashMap<>();
            properties.put("caption", caption);
            properties.put("hashtags", hashtags);
            properties.put("postId", postId);
            properties.put("username", username);

            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);
            createIndexRequest.mapping(mapping);
            elasticsearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }
        return "done";
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

    @RequestMapping(value = "doSearch/{queryString}", method = RequestMethod.GET)
    public Object getPostsByHashtags(@PathVariable String queryString) {
        return elasticSearchService.doSearch(queryString);
    }
}
