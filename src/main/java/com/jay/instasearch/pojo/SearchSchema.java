package com.jay.instasearch.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SearchSchema {
    private Long postId;
    private String username;
    private String caption;
    private List<String> hashtags;

//    public SearchSchema(Post post) {
//        this.postId = post.getId();
//        this.username = post.getUser().getUsername();
//        this.caption = post.getCaption();
//        this.hashtags = post.getTags();
//    }
}
