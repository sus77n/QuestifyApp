package com.example.iquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IQuizApplication {

    public static void main(String[] args) {
        SpringApplication.run(IQuizApplication.class, args);
    }

}