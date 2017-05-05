package com.qy.ftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@SpringBootApplication
@EnableWebSecurity
@EnableEurekaClient
public class FtpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtpServerApplication.class, args);
    }
}
