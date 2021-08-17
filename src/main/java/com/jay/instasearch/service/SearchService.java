package com.jay.instasearch.service;


import com.jay.instasearch.pojo.SearchSchema;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Component
public interface SearchService {
    @GetMapping("/search-schema/get-all-post")
    String getAllPost();
}
