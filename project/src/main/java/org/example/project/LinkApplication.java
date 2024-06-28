package org.example.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.example.project.dao.mapper")
@SpringBootApplication
public class LinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkApplication.class,args);
    }
}
