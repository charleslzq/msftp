package com.qy.ftp.endpoint.deploy;

import com.qy.ftp.support.OsEnum;
import com.qy.ftp.user.UserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liuzhengqi on 4/24/2017.
 */
@Configuration
@EnableConfigurationProperties(DeployFileProperties.class)
public class DeployFileEndpointConfiguration {

	@Autowired
	private UserProperties userProperties;

	@Autowired
	private DeployFileProperties deployFileProperties;

	@Bean
	public DeployFileEndpoint deployEndpoint() {
		DeployFileManager manager = new DeployFileManager(userProperties.getBaseDir().get(OsEnum.getOs()));
		return new DeployFileEndpoint(
				deployFileProperties.getId(),
				deployFileProperties.isSensitive(),
				deployFileProperties.isEnabled(),
				manager);
	}

	@Bean
	public DeployFileMvcEndpoint deployMvcEndpoint() {
		return new DeployFileMvcEndpoint(deployEndpoint());
	}
}
