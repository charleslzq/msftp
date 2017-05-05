package com.qy.ftp.endpoint.health;

import com.qy.ftp.core.FtpServerConfiguration;
import com.qy.ftp.core.FtpServerContainer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@ConditionalOnWebApplication
@Configuration
@AutoConfigureAfter(FtpServerConfiguration.class)
public class HealthConfiguration {

    @Bean
    public FtpHealthIndicator ftpHealthIndicator(FtpServerContainer ftpServerContainer) {
        return new FtpHealthIndicator(ftpServerContainer);
    }
}
