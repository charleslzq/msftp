package com.qy.ftp.manager;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by charleslzq on 17-5-5.
 */
@Slf4j
public class ServiceManager {
    private final ConcurrentHashMap<String, String> serviceRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Process> serviceTracker = new ConcurrentHashMap<>();

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

    public void start(String service) throws IOException {
        if (!serviceRegistry.containsKey(service)) {
            throw new IllegalArgumentException("Service startup script not configured");
        }
        if (serviceTracker.contains(service) && serviceTracker.get(service).isAlive()) {
            throw new IllegalArgumentException("Service Already In Run");
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-jar",
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
        if (!serviceTracker.containsKey(service)) {
            throw new IllegalArgumentException("Service Not In Running");
        }
        Process process = serviceTracker.get(service);
        if (process.isAlive()) {
            process.destroy();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                log.error("Something Error Happen When Stopping Process", e);
            } finally {
                serviceTracker.remove(service);
            }
            if (process.exitValue() != 0) {
                throw new IllegalStateException("Process Terminated In Error State " + process.exitValue());
            }
        }
    }


    public String status(String service) {
        boolean registerState = serviceRegistry.containsKey(service);
        if (registerState) {
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
