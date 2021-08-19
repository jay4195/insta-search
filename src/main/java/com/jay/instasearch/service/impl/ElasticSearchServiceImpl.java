package com.jay.instasearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.ElasticSearchService;
import com.jay.instasearch.service.RemoteSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {
    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

    private static final String SEARCH_INDICE = "post-info";

    private static final int SEARCH_RESULT_COUNTS = 12;

    private static final int SEARCH_RESULT_TIMEOUT = 60;

    @Autowired
    RemoteSearchService searchService;

    @Autowired
    RestHighLevelClient elasticsearchClient;

    @Override
    public boolean createIndex() {
        GetIndexRequest request = new GetIndexRequest(SEARCH_INDICE);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        boolean exists = false;
        try {
            exists = elasticsearchClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("Query Index Exist Exception");
            e.printStackTrace();
            return false;
        }
        if (exists) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(SEARCH_INDICE);
            try {
                elasticsearchClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error("Delete Index Exception");
                e.printStackTrace();
                return false;
            }
        } else {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(SEARCH_INDICE);
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
            try {
                elasticsearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error("Create Index Exception");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean updatePostInfo() {
        HashSet<Long> esPostSet = new HashSet<>();

        SearchRequest searchRequest = new SearchRequest(SEARCH_INDICE);
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.storedFields(new LinkedList<>());
        searchSourceBuilder.size(100);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("[UPDATE] search exception");
            e.printStackTrace();
            return false;
        }
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        addSearchHits(searchHits, esPostSet);
        while(searchHits != null && searchHits.length > 0) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            try {
                searchResponse = elasticsearchClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error("[UPDATE] search exception");
                e.printStackTrace();
                return false;
            }
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
            addSearchHits(searchHits, esPostSet);
        }
        List<Long> esPostList = esPostSet.stream().toList();
        System.out.println(esPostList);
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = null;
        try {
            clearScrollResponse = elasticsearchClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.info("[UPDATE] clear scroll response Exception");
            e.printStackTrace();
        }
        boolean succeeded = clearScrollResponse.isSucceeded();
        return true;
    }

    private void addSearchHits(SearchHit[] searchHits, HashSet<Long> set) {
        for (SearchHit searchHit : searchHits) {
            set.add(Long.parseLong(searchHit.getId()));
        }
    }

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
