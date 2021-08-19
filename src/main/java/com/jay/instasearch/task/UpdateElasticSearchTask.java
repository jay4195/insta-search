package com.jay.instasearch.task;

import com.jay.instasearch.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateElasticSearchTask {
    @Autowired
    ElasticSearchService elasticSearchService;

    @Scheduled(cron = "0 0 */2 * * ?" )
    public void autoUpdate() {
        if (elasticSearchService.updateElasticsearch()) {
            log.info("[Spring Scheduling] auto update done.");
        } else {
            log.warn("[Spring Scheduling] auto update error.");
        }
    }
}
