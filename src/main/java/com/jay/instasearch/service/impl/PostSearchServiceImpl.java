package com.jay.instasearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.PostSearchService;
import com.jay.instasearch.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class PostSearchServiceImpl implements PostSearchService {
    @Autowired
    SearchService searchService;

    @Autowired
    RestHighLevelClient elasticsearchClient;

    @Override
    public List<SearchSchema> getAllPosts() {
        SearchRequest searchRequest = new SearchRequest("post-info");
        SearchResponse searchResponse = null;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.warn("Search All Posts Exception");
            return null;
        }
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<SearchSchema> searchResults = new LinkedList<>();
        for (SearchHit searchHit : searchHits) {
            searchResults.add(JSON.parseObject(searchHit.getSourceAsString(), SearchSchema.class));
        }
        return searchResults;
    }
}
