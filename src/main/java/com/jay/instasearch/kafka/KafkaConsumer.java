package com.jay.instasearch.kafka;

import com.alibaba.fastjson.JSON;
import com.jay.instasearch.pojo.Post;
import com.jay.instasearch.pojo.SearchSchema;
import com.jay.instasearch.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KafkaConsumer {
    @Autowired
    ElasticSearchService elasticSearchService;
    // 消费监听
    @KafkaListener(topics = {"post-after"})
    public void onPost(ConsumerRecord<?, ?> record){
        Post post = JSON.parseObject((String) record.value(), Post.class);
        log.info("[Kafka Listener] on Post id: {}", post.getId());
        SearchSchema searchSchema = new SearchSchema();
        searchSchema.setSearchSchemaByPost(post);
        if (elasticSearchService.insertPostInfo(searchSchema)) {
            log.info("[Kafka Listener] Post id: {} inserted...", post.getId());
        } else {
            log.warn("[Kafka Listener] Post id: {} inserting failed!", post.getId());
        }
    }

    @KafkaListener(topics = {"delete-post"})
    public void onDeletePost(ConsumerRecord<?, ?> record){
        String value = (String) record.value();
        Long id = Long.parseLong(value);
        log.info("[Kafka Listener] on Delete Post id: {}", Long.parseLong(value));
        if (elasticSearchService.deletePostInfo(id)) {
            log.info("[Kafka Listener] Post id: {} deleted...", id);
        } else {
            log.warn("[Kafka Listener] Post id: {} deleting failed!", id);
        }
    }
}