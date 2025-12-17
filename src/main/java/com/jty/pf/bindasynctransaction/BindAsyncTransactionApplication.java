package com.jty.pf.bindasynctransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BindAsyncTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BindAsyncTransactionApplication.class, args);
    }

}
