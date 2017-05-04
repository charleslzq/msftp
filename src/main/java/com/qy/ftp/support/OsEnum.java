package com.qy.ftp.support;

import java.util.stream.Stream;

/**
 * Created by liuzhengqi on 4/14/2017.
 */
public enum OsEnum {
	WINDOWS,
	UNIX,
	LINUX;

	public static OsEnum getOs() {
		return Stream.of(values())
				.filter(OsEnum::active)
				.findAny()
				.orElseThrow(() -> new IllegalStateException("Unsupported Operating System"));
	}

	public boolean active() {
		return System.getProperty("os.name").toUpperCase().contains(toString());
	}
}
