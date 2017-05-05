package com.qy.ftp.support;

import com.google.common.collect.Lists;
import com.qy.ftp.manager.ServiceManager;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by charleslzq on 17-5-5.
 */
@Slf4j
public class OsServiceUtil {
    private static final Pattern QY_PROCESS_PATTERN =
            Pattern.compile("^(\\d+)\\s.+\\s-D" + ServiceManager.PROCESS_KEY + "=(\\S+)(\\s|$)");
    private static final Pattern COMMON_PROCESS_PATTERN =
            Pattern.compile("^(\\d+)");
    private static EnumMap<OsEnum, Function<String, List<String>>> killFunctions = new EnumMap<>(OsEnum.class);

    static {
        killFunctions.put(OsEnum.LINUX, pid -> Lists.newArrayList("kill", "-9", pid));
        killFunctions.put(OsEnum.WINDOWS, pid -> Lists.newArrayList("taskkill", "/pid", pid, "/f"));
    }

    private static Optional<String> findMatchService(String line, Collection<String> services) {
        return services.stream()
                .filter(service -> line.contains(service))
                .findAny();
    }

    public static Map<String, String> detectRunningService(Collection<String> services) {
        ProcessBuilder processBuilder = new ProcessBuilder("jps", "-v");
        try {
            Process process = processBuilder.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                Map<String, String> result = new HashMap<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.debug("Running Process: {}", line);
                    Matcher matcher = QY_PROCESS_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        String pid = matcher.group(1);
                        String service = matcher.group(2);
                        result.put(service, pid);
                    } else {
                        Optional<String> service = findMatchService(line, services);
                        if (service.isPresent()) {
                            Matcher matcher1 = COMMON_PROCESS_PATTERN.matcher(line);
                            if (matcher1.matches()) {
                                result.put(service.get(), matcher1.group(1));
                            }
                        }
                    }
                }
                return result;
            }
        } catch (IOException e) {
            log.error("Error when detect running services", e);
            return Collections.emptyMap();
        }
    }

    public static boolean checkStatus(String pid) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("jps", "-v");
        Process process = processBuilder.start();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String processId = COMMON_PROCESS_PATTERN.matcher(line).group(1);
                if (processId == pid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean kill(String pid) {
        ProcessBuilder processBuilder = new ProcessBuilder(killFunctions.get(OsEnum.getOs()).apply(pid));
        try {
            processBuilder.start();
            return true;
        } catch (IOException e) {
            log.error("Error when killing process " + pid, e);
            return false;
        }
    }

}
