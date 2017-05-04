package com.qy.ftp.core;

import lombok.Data;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.impl.DefaultConnectionConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@ConfigurationProperties(
		prefix = "qy.ftp.server.connect"
)
@Data
public class FtpServerProperties {
	/**
	 * 最大登录数
	 */
	private int maxLogins = 10;
	/**
	 * 启用匿名登录
	 */
	private boolean anonymous = true;
	/**
	 * 最大匿名登录数
	 */
	private int maxAnonymous = 50;
	/**
	 * 最大允许登录失败数
	 */
	private int maxFail = 3;
	/**
	 * 登录失败延迟
	 */
	private int failDelay = 100;
	/**
	 * 最大线程数
	 */
	private int maxThreads = 20;

	public ConnectionConfig createConnectionConfig() {
		return new DefaultConnectionConfig(
				anonymous,
				failDelay,
				maxLogins,
				maxAnonymous,
				maxFail,
				maxThreads);
	}
}
