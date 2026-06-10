package com.rikkeibank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RikkeiBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(RikkeiBankApplication.class, args);
    }

}
