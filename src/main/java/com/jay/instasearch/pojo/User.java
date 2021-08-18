package com.jay.instasearch.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty("_id")
    private Long id;

    private String avatar;

    private String username;

    private String password;

    private String fullname;

    private String email;

    private String website;

    private String bio;

    private Date createdAt;
}
