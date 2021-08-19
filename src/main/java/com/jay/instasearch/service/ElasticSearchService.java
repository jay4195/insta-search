package com.jay.instasearch.service;

import com.jay.instasearch.pojo.SearchSchema;

import java.util.List;

public interface ElasticSearchService {
    boolean createIndex();

    boolean updatePostInfo();

    List<SearchSchema> getAllPosts();

    List<SearchSchema> getPostsByHashtags(List<String> hashtags);

    List<SearchSchema> getPostsByUsername(String username);

    List<SearchSchema> getPostByCaption(String caption);

}
