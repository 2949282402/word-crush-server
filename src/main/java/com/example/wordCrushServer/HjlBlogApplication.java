package com.example.wordCrushServer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.wordCrushServer.mapper")
public class HjlBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(HjlBlogApplication.class, args);
    }
}
