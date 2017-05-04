package com.qy.ftp.endpoint.health;

import com.qy.ftp.core.FtpServerContainer;
import com.qy.ftp.support.OsEnum;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import java.util.Optional;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
public class FtpHealthIndicator extends AbstractHealthIndicator {
	private final FtpServerContainer ftpServerContainer;

	public FtpHealthIndicator(FtpServerContainer ftpServerContainer) {
		this.ftpServerContainer = ftpServerContainer;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (ftpServerContainer.isRunning()) {
			builder.up();
		} else {
			builder.down();
		}
		builder.withDetail("os", OsEnum.getOs())
				.withDetail("baseDir", ftpServerContainer.getRootPath());
		Optional<FtpletContext> ftpletContextOptional = ftpServerContainer.getFtpletContext();
		if (ftpletContextOptional.isPresent()) {
			builder.withDetail("statics", ftpletContextOptional.get().getFtpStatistics());
		}
	}
}
