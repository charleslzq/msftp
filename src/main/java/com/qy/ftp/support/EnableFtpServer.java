package com.qy.ftp.support;

import com.qy.ftp.core.FtpServerConfiguration;
import com.qy.ftp.endpoint.deploy.DeployFileEndpointConfiguration;
import com.qy.ftp.endpoint.health.HealthConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        FtpServerConfiguration.class,
        HealthConfiguration.class,
        DeployFileEndpointConfiguration.class
})
public @interface EnableFtpServer {
}
