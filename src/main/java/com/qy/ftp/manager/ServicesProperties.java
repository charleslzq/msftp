package com.qy.ftp.manager;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuzhengqi on 17-5-5.
 */
@Data
@ConfigurationProperties(prefix = "qy.remote.managed")
public class ServicesProperties {
    private Map<String, String> services = new HashMap<>();
}
