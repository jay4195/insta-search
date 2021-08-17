package com.jay.instasearch.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
public class UserSchema {
    @JsonProperty("_id")
    private Long id;
    @JsonProperty("isMe")
    private boolean isMe = false;
    @JsonProperty("isFollowing")
    private boolean followingStatus = false;
    private String fullname;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private String bio;
    private String website;
    private List<UserSchema> followers = new LinkedList<>();
    private List<UserSchema> following = new LinkedList<>();
    private List<Post> posts = new LinkedList<>();
    private List<Post> savedPosts = new LinkedList<>();
    private Long followersCount = 0L;
    private Long followingCount = 0L;
    private Long postCount = 0L;
    private Date createdAt;
    public UserSchema(User user) {
        this.id = user.getId();
        this.fullname = user.getFullname();
        this.avatar = user.getAvatar();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.bio = user.getBio();
        this.website = user.getWebsite();
    }
}
