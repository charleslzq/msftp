package com.qy.ftp.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.SmartLifecycle;

import java.util.Map;
import java.util.Optional;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@Slf4j
public class FtpServerContainer implements SmartInitializingSingleton, SmartLifecycle, DisposableBean {
    private final FtpServerFactory ftpServerFactory;
    @Getter
    private final String rootPath;
    private FtpServer ftpServer;
    @Setter
    private boolean autoStartup = true;
    @Getter
    @Setter
    private int phase = 0;

    public FtpServerContainer(FtpServerFactory ftpServerFactory, String rootPath) {
        this.ftpServerFactory = ftpServerFactory;
        this.rootPath = rootPath;
    }

    public Optional<FtpletContext> getFtpletContext() {
        if (ftpServer instanceof DefaultFtpServer) {
            return Optional.of(((DefaultFtpServer) ftpServer).getServerContext());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (ftpServerFactory.getListeners().size() == 0) {
            ftpServerFactory.addListener("default", (new ListenerFactory()).createListener());
        }
        ftpServer = ftpServerFactory.createServer();
        if (this.isAutoStartup()) {
            this.start();
            Map<String, Listener> listeners = ftpServerFactory.getListeners();
            listeners.values().stream().forEach(
                    listener -> log.info("Using Port: {}", listener.getPort())
            );
        }
    }

    @Override
    public void destroy() throws Exception {
        this.stop(() -> log.info("Ftp Server Stopped"));
    }

    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public void stop(Runnable runnable) {
        if (ftpServer != null) {
            ftpServer.stop();
            if (null != runnable) {
                runnable.run();
            }
        }
    }

    @Override
    public void start() {
        if (ftpServer != null) {
            try {
                ftpServer.start();
                log.info("Ftp Server Started Successfully");
            } catch (FtpException e) {
                log.error("Error when start ftp server", e);
            }
        }
    }

    @Override
    public void stop() {
        stop(null);
    }

    @Override
    public boolean isRunning() {
        return ftpServer == null ? false : !ftpServer.isStopped() && !ftpServer.isSuspended();
    }
}
