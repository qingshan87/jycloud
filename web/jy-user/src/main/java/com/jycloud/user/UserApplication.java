package com.jycloud.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

// @EnableCaching         // 使用 @Cacheable 时取消注释
// @MapperScan("com.jycloud.**.mapper")
@ComponentScan("com.jycloud.**")
@EnableFeignClients(basePackages = "com.jycloud.**.feign")  // 精确扫描 Feign 接口
@EnableDiscoveryClient  // 使用服务注册时取消注释
@EnableAsync           // 使用 @Async 时取消注释
@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}

