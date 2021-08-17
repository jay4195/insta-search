package com.jay.instasearch.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty("_id")
    private Long id;

    //@Length(max = 75, message = "avatar should less than 50")
    private String avatar;

    @Length(max = 25, message = "username should less than 25")
    private String username;

    @Length(max = 25, message = "password should less than 25")
    private String password;

    @Length(max = 25, message = "fullname should less than 25")
    private String fullname;

    @Length(max = 50, message = "email should less than 50")
    private String email;

    @Length(max = 50, message = "website should less than 50")
    private String website;

    @Length(max = 100, message = "bio should less than 50")
    private String bio;

    private Date createdAt;
}
