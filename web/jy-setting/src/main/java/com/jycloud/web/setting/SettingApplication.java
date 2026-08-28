package com.jycloud.web.setting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.jycloud.**.mapper")
@ComponentScan({"com.jycloud.web.setting", "com.jycloud.service", "com.jycloud.tool"})
@EnableFeignClients(basePackages = "com.jycloud.**.feign")  // 精确扫描 Feign 接口
@EnableDiscoveryClient  // 使用服务注册时取消注释
@EnableAsync           // 使用 @Async 时取消注释
@SpringBootApplication
public class SettingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SettingApplication.class, args);
    }
}
