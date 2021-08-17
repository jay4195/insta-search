package com.jay.instasearch.kafka;

import com.alibaba.fastjson.JSON;
import com.jay.instasearch.pojo.Post;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {
    // 消费监听
    @KafkaListener(topics = {"post"})
    public void onPost(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        //System.out.println("简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());
        Post post = JSON.parseObject((String) record.value(), Post.class);
    }

    @KafkaListener(topics = {"delete-post"})
    public void onDeletePost(ConsumerRecord<?, ?> record){
        String value = (String) record.value();
    }
}