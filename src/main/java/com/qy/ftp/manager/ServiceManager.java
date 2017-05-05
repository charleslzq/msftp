package com.qy.ftp.manager;

import com.qy.ftp.support.OsServiceUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by charleslzq on 17-5-5.
 */
@Slf4j
public class ServiceManager {
    public static final String PROCESS_KEY = "QyProcessName";
    private final ConcurrentHashMap<String, String> serviceRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Process> serviceTracker = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> alreadyRunningServices = new ConcurrentHashMap<>();

    public List<String> getServices() {
        return new ArrayList<>(serviceRegistry.keySet());
    }

    public List<String> getActiveServices() {
        return new ArrayList<>(serviceTracker.keySet());
    }

    public boolean register(String service, String command, boolean override) {
        if (override) {
            serviceRegistry.put(service, command);
            return true;
        } else {
            return serviceRegistry.putIfAbsent(service, command) == null;
        }
    }

    public void detectRunningServices() {
        alreadyRunningServices.putAll(OsServiceUtil.detectRunningService(serviceRegistry.keySet()));
    }

    public void start(String service) throws IOException {
        if (!serviceRegistry.containsKey(service)) {
            throw new IllegalArgumentException("Service startup script not configured");
        }
        if ((serviceTracker.contains(service) && serviceTracker.get(service).isAlive())
                || alreadyRunningServices.containsKey(service)) {
            throw new IllegalArgumentException("Service Already In Run");
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-jar",
                    "-D" + PROCESS_KEY + "=" + service,
                    serviceRegistry.get(service)
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            serviceTracker.put(service, process);
        } catch (IOException e) {
            log.error("Error when start service " + service, e);
            throw e;
        }
    }

    public void shutdown(String service) {
        if (!serviceTracker.containsKey(service) && !alreadyRunningServices.containsKey(service)) {
            throw new IllegalArgumentException("Service Not In Running");
        }
        if (serviceTracker.containsKey(service)) {
            Process process = serviceTracker.get(service);
            if (process.isAlive()) {
                process.destroy();
                try {
                    process.waitFor(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("Something Error Happen When Stopping Process", e);
                } finally {
                    serviceTracker.remove(service);
                }
                if (process.exitValue() != 0) {
                    throw new IllegalStateException("Process Terminated In Error State " + process.exitValue());
                }
            }
        } else if (alreadyRunningServices.containsKey(service)) {
            OsServiceUtil.kill(alreadyRunningServices.get(service));
            alreadyRunningServices.remove(service);
        }
    }


    public String status(String service) {
        boolean registerState = serviceRegistry.containsKey(service);
        if (registerState) {
            if (alreadyRunningServices.containsKey(service)) {
                return "RUNNING NORMALLY";
            }
            boolean startState = serviceTracker.containsKey(service);
            if (startState) {
                return serviceTracker.get(service).isAlive() ?
                        "RUNNING NORMALLY" :
                        serviceTracker.get(service).exitValue() == 0 ?
                                "EXIT NORMALLY" : "EXIT WITH ERROR";
            } else {
                return "REGISTERED & NOT_STARTED";
            }
        } else {
            return "NOT_REGISTERED";
        }
    }

}
