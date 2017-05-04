package com.qy.ftp.user;

import lombok.Data;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@Data
public class FtpUser {
	/**
	 * 用户名
	 */
	private String name = null;
	/**
	 * 密码
	 */
	private String password = null;
	/**
	 * 最大空闲时间，单位：秒
	 */
	private int maxIdleSec = 300;
	/**
	 * 是否启用
	 */
	private boolean isEnabled = true;
	/**
	 * 写权限
	 */
	private boolean write = true;
	/**
	 * 最大同时登录数
	 */
	private int maxLogin = 5;
	/**
	 * 最大允许IP数
	 */
	private int maxIp = 50;
	/**
	 * 最大上传速率，单位：byte/s
	 */
	private int upload = 480000;
	/**
	 * 最大下载速率，单位:byte/s
	 */
	private int download = 0;

	public User createUser(String baseDir) {
		BaseUser baseUser = new BaseUser();
		String homeDir = baseDir + File.separator + name;
		File dir = new File(homeDir);
		dir.mkdir();
		baseUser.setName(name);
		baseUser.setPassword(password);
		baseUser.setHomeDirectory(homeDir);
		baseUser.setMaxIdleTime(maxIdleSec);
		baseUser.setEnabled(isEnabled);
		List<Authority> authorities = new ArrayList<>();
		authorities.add(new ConcurrentLoginPermission(maxLogin, maxIp));
		authorities.add(new TransferRatePermission(download, upload));
		if (write) {
			authorities.add(new WritePermission());
		}
		baseUser.setAuthorities(authorities);
		return baseUser;
	}
}
