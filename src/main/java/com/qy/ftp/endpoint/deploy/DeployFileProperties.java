package com.qy.ftp.endpoint.deploy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liuzhengqi on 4/24/2017.
 */
@ConfigurationProperties(prefix = "endpoints.deployFile")
@Data
public class DeployFileProperties {
    /**
     * 发布端点id
     */
    private String id = "deployFile";
    /**
     * 启用端点安全机制
     */
    private boolean sensitive = true;
    /**
     * 启用端点
     */
    private boolean enabled = true;
}
