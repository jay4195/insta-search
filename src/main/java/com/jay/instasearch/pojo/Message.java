package com.jay.instasearch.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Message {
    @JsonProperty("isSender")
    private boolean senderIsMe;
    private User sender;
    private User receiver;
    private String text;
    private Date createdAt;
}
