package com.swivel.cc.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableDiscoveryClient
@ServletComponentScan
@SpringBootApplication
@EnableResourceServer
@EnableScheduling
@EnableAsync
public class QponCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(QponCoreApplication.class, args);
    }

}
