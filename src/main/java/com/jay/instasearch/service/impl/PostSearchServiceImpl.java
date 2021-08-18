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
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PostSearchServiceImpl implements PostSearchService {
    private static final String SEARCH_INDICE = "post-info";

    private static final int SEARCH_RESULT_COUNTS = 12;

    private static final int SEARCH_RESULT_TIMEOUT = 60;

    @Autowired
    SearchService searchService;

    @Autowired
    RestHighLevelClient elasticsearchClient;

    //elastic search只返回前10条记录
    @Override
    public List<SearchSchema> getAllPosts() {
        SearchRequest searchRequest = new SearchRequest(SEARCH_INDICE);
        SearchResponse searchResponse = null;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.warn("Search All Posts Exception");
            return null;
        }
        List<SearchSchema> searchResults = parseSearchResponse(searchResponse);
        log.info("search records: {}", searchResults.size());
        return searchResults;
    }

    @Override
    public List<SearchSchema> getPostsByUsername(String username) {
        List<SearchSchema> searchResults = getPostsByAccurateUsername(username);
        if (searchResults.size() == 0) {
            searchResults = getPostsByFuzzyUsername(username);
        }
        return searchResults;
    }

    @Override
    public List<SearchSchema> getPostByCaption(String caption) {
        MatchQueryBuilder matchQueryBuilder =
                new MatchQueryBuilder("caption", caption)
                        .fuzziness(Fuzziness.AUTO);

        List<SearchSchema> searchResults = null;
        SearchResponse searchResponse = sendQueryRequest(matchQueryBuilder);
        if (searchResponse != null) {
            searchResults = parseSearchResponse(searchResponse);
        } else {
            return searchResults;
        }
        log.info("Search Posts By Caption records: {}", searchResults.size());
        return searchResults;
    }


    @Override
    public List<SearchSchema> getPostsByHashtags(List<String> hashtags) {
        int hashTagsCounts = Math.min(hashtags.size(), 3);
        HashMap<Long, SearchSchema> resultMap = new HashMap<>();
        for (int i = 0; i < hashTagsCounts; i++) {
            MatchQueryBuilder matchQueryBuilder =
                    new MatchQueryBuilder("hashtags", hashtags.get(i))
                            .fuzziness(Fuzziness.AUTO);
            SearchResponse searchResponse = sendQueryRequest(matchQueryBuilder);
            if (searchResponse != null) {
                List<SearchSchema> result = parseSearchResponse(searchResponse);
                for (SearchSchema r : result) {
                    if (!resultMap.containsKey(r.getPostId())) {
                        resultMap.put(r.getPostId(), r);
                    }
                }
            }
        }
        log.info("Search Posts By Hashtags records: {}", resultMap.size());
        return resultMap.values().stream().toList();
    }

    private List<SearchSchema> getPostsByAccurateUsername(String username) {
        List<SearchSchema> searchResults = null;
        SearchResponse searchResponse = sendQueryRequest(QueryBuilders.termQuery("username", username));
        if (searchResponse != null) {
            searchResults = parseSearchResponse(searchResponse);
        } else {
            return searchResults;
        }
        log.info("Search Posts By Accurate Username records: {}", searchResults.size());
        return searchResults;
    }

    private List<SearchSchema> getPostsByFuzzyUsername(String username) {
        MatchQueryBuilder matchQueryBuilder =
                new MatchQueryBuilder("username", username)
                    .fuzziness(Fuzziness.AUTO)
                    .prefixLength(3)
                    .maxExpansions(10);
        List<SearchSchema> searchResults = null;
        SearchResponse searchResponse = sendQueryRequest(matchQueryBuilder);
        if (searchResponse != null) {
            searchResults = parseSearchResponse(searchResponse);
        } else {
            return searchResults;
        }
        log.info("Search Posts By Fuzzy Username records: {}", searchResults.size());
        return searchResults;
    }

    private SearchResponse sendQueryRequest(QueryBuilder queryBuilder) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(SEARCH_RESULT_COUNTS);
        sourceBuilder.timeout(new TimeValue(SEARCH_RESULT_TIMEOUT, TimeUnit.SECONDS));
        sourceBuilder.query(queryBuilder);

        SearchRequest searchRequest = new SearchRequest(SEARCH_INDICE);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("Elastic Search Client Exception");
            return null;
        }
        return searchResponse;
    }

    private List<SearchSchema> parseSearchResponse(SearchResponse searchResponse) {
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<SearchSchema> searchResults = new LinkedList<>();
        for (SearchHit searchHit : searchHits) {
            searchResults.add(JSON.parseObject(searchHit.getSourceAsString(), SearchSchema.class));
        }
        return searchResults;
    }

}
