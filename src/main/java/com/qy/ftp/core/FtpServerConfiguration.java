package com.qy.ftp.core;

import com.qy.ftp.support.OsEnum;
import com.qy.ftp.user.UserProperties;
import com.qy.ftp.user.YmlUserManager;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@Configuration
@EnableConfigurationProperties({
        FtpServerProperties.class,
        UserProperties.class
})
public class FtpServerConfiguration {

    @Autowired
    private FtpServerProperties ftpServerProperties;

    @Autowired
    private UserProperties userProperties;

    private String rootPath;

    @Bean
    @ConditionalOnMissingBean
    public UserManager ymlUserManager() {
        rootPath = userProperties.getBaseDir().get(OsEnum.getOs());
        if (!StringUtils.hasText(rootPath)) {
            throw new IllegalStateException("Root Path Not Configured");
        }
        File ftpRoot = new File(rootPath);
        ftpRoot.mkdir();
        return new YmlUserManager(
                userProperties.getUsers().stream()
                        .filter(ftpUser -> null != ftpUser.getName() && null != ftpUser.getPassword())
                        .map(ftpUser -> ftpUser.createUser(rootPath))
                        .collect(Collectors.toMap(User::getName, Function.identity())),
                userProperties.getAdmin()
        );
    }

    @Bean
    public FtpServerContainer ftpServerContainer(UserManager userManager) {
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        ftpServerFactory.setUserManager(userManager);
        ftpServerFactory.setConnectionConfig(ftpServerProperties.createConnectionConfig());
        return new FtpServerContainer(ftpServerFactory, rootPath);
    }
}
