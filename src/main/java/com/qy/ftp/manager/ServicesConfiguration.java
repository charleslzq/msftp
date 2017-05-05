package com.qy.ftp.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by charleslzq on 17-5-5.
 */
@Configuration
@EnableConfigurationProperties(ServicesProperties.class)
public class ServicesConfiguration {

    @Autowired
    private ServicesProperties servicesProperties;

    @Bean
    public ServiceManager serviceManager() {
        ServiceManager serviceManager = new ServiceManager();
        servicesProperties.getServices()
                .forEach((service, script) ->
                        serviceManager.register(
                                service,
                                script,
                                true
                        )
                );
        return serviceManager;
    }
}
