package com.qy.ftp.user;

import com.qy.ftp.support.OsEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@ConfigurationProperties(
		prefix = "qy.ftp.server"
)
@Data
public class UserProperties {
	private String admin = "admin";
	private EnumMap<OsEnum, String> baseDir = new EnumMap<>(OsEnum.class);
	private List<FtpUser> users = new ArrayList<>();
}
