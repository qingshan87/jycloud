package com.jycloud.web.ucenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.jycloud.**.mapper")
@ComponentScan({"com.jycloud.web.ucenter", "com.jycloud.service.user", "com.jycloud.tool"})
@EnableFeignClients(basePackages = "com.jycloud.**.feign")  // 精确扫描 Feign 接口
@EnableDiscoveryClient
@EnableAsync
@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}

