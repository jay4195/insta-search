package com.jay.instasearch.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class Post {
    @JsonProperty("_id")
    private Long id = 0L;

    private User user;

    private String caption;

    @JsonProperty("isLiked")
    private boolean isLiked = false;

    @JsonProperty("isSaved")
    private boolean isSaved = false;

    @JsonProperty("isMine")
    private boolean isMine = false;

    private List<String> tags = new LinkedList<>();

    private List<String> files = new LinkedList<>();

    private Long likesCount = 0L;

    private List<Comment> comments = new LinkedList<>();

    private Long commentsCount = 0L;

    private Date createdAt;
}
