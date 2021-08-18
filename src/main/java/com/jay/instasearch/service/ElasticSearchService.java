package com.jay.instasearch.service;

import com.jay.instasearch.pojo.SearchSchema;

import java.util.List;

public interface ElasticSearchService {
    List<Long> search(String searchInput);
    List<SearchSchema> doSearch(String searchInput);
}
