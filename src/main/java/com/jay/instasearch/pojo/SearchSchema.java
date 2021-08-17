package com.jay.instasearch.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SearchSchema {
    private Long postId;
    private String username;
    private String caption;
    private List<String> hashtags;
}
