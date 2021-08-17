package com.jay.instasearch.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private Long postId;
    private Long uid;
    @JsonProperty("user")
    private User user;
    @JsonProperty("text")
    private String comment;
    private Date createdAt;
}
