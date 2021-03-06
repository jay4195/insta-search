package com.jay.instasearch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.InstaSearchService;
import com.jay.instasearch.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.Scroll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class InstaSearchServiceImpl implements InstaSearchService {
    @Autowired
    ElasticSearchService elasticSearchService;

    @Override
    public List<Long> search(String searchInput) {
        List<SearchSchema> result = doSearch(searchInput);
        List<Long> resultIdList = new LinkedList<>();
        for (SearchSchema searchSchema : result) {
            resultIdList.add(searchSchema.getPostId());
        }
        return resultIdList;
    }

    @Override
    public List<SearchSchema> doSearch(String searchInput) {
        JSONObject jsonObject = splitSearchInput(searchInput);
        Map<Long, SearchSchema> resultMap = new TreeMap<>((o1, o2) -> {
            //DESC
            if (o1 < o2) {
                return 1;
            }
            if (o1 > o2) {
                return -1;
            }
            return 0;
        });
        List<SearchSchema> resultList = new LinkedList<>();
        if (jsonObject.containsKey("username")) {
            resultList.addAll(elasticSearchService.getPostsByUsername((String) jsonObject.get("username")));
        }
        if (resultList.size() > 0) {
            return resultList;
        }
        if (jsonObject.containsKey("caption")) {
            resultList.addAll(elasticSearchService.getPostByCaption((String) jsonObject.get("caption")));
        }
        if (jsonObject.containsKey("hashtags")) {
            resultList.addAll(elasticSearchService.getPostsByHashtags((List<String>) jsonObject.get("hashtags")));
        }
        for (SearchSchema schema : resultList) {
            resultMap.putIfAbsent(schema.getPostId(), schema);
        }
        log.info("[Search Done] search: [{}] {} results", searchInput,resultMap.size());
        return resultMap.values().stream().toList();
    }

    private JSONObject splitSearchInput(String searchInput) {
        JSONObject jsonObject = new JSONObject();
        String[] queryList = searchInput.split(" ");
        List<String> hashtags = new LinkedList<>();
        if (queryList.length == 1) {
            if (queryList[0].startsWith("#")) {
                hashtags.add(queryList[0]);
                jsonObject.put("hashtags", hashtags);
            } else {
                jsonObject.put("username", queryList[0]);
                jsonObject.put("caption", queryList[0]);
                hashtags.add("#" + queryList[0]);
                jsonObject.put("hashtags", hashtags);
            }
            return jsonObject;
        }
        String caption = "";
        StringBuilder captionBuilder = new StringBuilder();
        for (String query : queryList) {
            if (query.startsWith("#")) {
                hashtags.add(query);
            } else {
                if (captionBuilder.isEmpty()) {
                    captionBuilder.append(query);
                } else {
                    captionBuilder.append(" ").append(query);
                }
                hashtags.add("#" + query);
            }
        }
        if (!captionBuilder.isEmpty()) {
            caption = captionBuilder.toString();
            jsonObject.put("caption", caption);
        }
        if (hashtags.size() != 0) {
            jsonObject.put("hashtags", hashtags);
        }
        return jsonObject;
    }
}
