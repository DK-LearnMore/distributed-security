package com.dk.security.distributed.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //注册服务
public class UaaMain {
    public static void main(String[] args) {
        SpringApplication.run(UaaMain.class,args);
    }
}
