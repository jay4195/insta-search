package com.jay.instasearch.service;

import com.jay.instasearch.pojo.SearchSchema;

import java.util.List;

public interface ElasticSearchService {
    boolean createElasticsearchIndex();

    boolean updateElasticsearch();

    boolean insertPostInfo(SearchSchema searchSchema);

    boolean deletePostInfo(Long postId);

    List<SearchSchema> getPostsFromDatabase();

    List<SearchSchema> getPostsByHashtags(List<String> hashtags);

    List<SearchSchema> getPostsByUsername(String username);

    List<SearchSchema> getPostByCaption(String caption);

}
