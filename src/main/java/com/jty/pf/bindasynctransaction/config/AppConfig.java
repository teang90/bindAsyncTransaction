package com.jty.pf.bindasynctransaction.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.jty.pf.bindasynctransaction.transaction.TransactionBindingManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager =
                new CaffeineCacheManager("txManagerCache");

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(1_000)
                        .maximumSize(10_000)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .recordStats()
        );

        return cacheManager;
    }


    @Bean
    public TransactionBindingManager transactionBindingManager(CacheManager cacheManager){
        return new TransactionBindingManager(cacheManager);
    }
}
