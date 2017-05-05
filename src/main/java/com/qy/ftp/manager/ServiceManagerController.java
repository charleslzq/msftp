package com.qy.ftp.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by charleslzq on 17-5-5.
 */
@Slf4j
@RestController
@RequestMapping("/services")
public class ServiceManagerController {

    @Autowired
    private ServiceManager serviceManager;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<String> services(
            @RequestParam(value = "active", required = false) final boolean active
    ) {
        return active ? serviceManager.getActiveServices() : serviceManager.getServices();
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public Map<String, String> servicesStatus(
            @RequestParam(value = "active", required = false) final boolean active
    ) {
        List<String> services = services(active);
        return services.stream().collect(Collectors.toMap(
                service -> service,
                service -> serviceManager.status(service)
        ));
    }

    @RequestMapping(value = "/{service}/{cmd}", method = RequestMethod.POST)
    public String runCmd(
            @PathVariable("service") final String service,
            @PathVariable("cmd") final String cmd
    ) {
        return ServiceCommand.valueOf(cmd.toUpperCase()).run(serviceManager, service);
    }

    enum ServiceCommand {
        START {
            @Override
            protected void doRun(ServiceManager manager, String service) throws IOException {
                manager.start(service);
            }
        },
        SHUTDOWN {
            @Override
            protected void doRun(ServiceManager manager, String service) throws IOException {
                manager.shutdown(service);
            }
        },
        RESTART {
            @Override
            protected void doRun(ServiceManager manager, String service) throws IOException {
                manager.restart(service);
            }
        },
        STATUS {
            @Override
            public String run(ServiceManager manager, String service) {
                return manager.status(service);
            }
        };

        public String run(ServiceManager manager, String service) {
            String baseString = "Service " + service + " " + StringUtils.capitalize(name().toLowerCase());
            try {
                doRun(manager, service);
                return baseString + " In Success";
            } catch (Exception e) {
                String errorString = baseString + " Failed";
                log.error(errorString, e);
                return errorString + ":" + e.getMessage();
            }
        }

        protected void doRun(ServiceManager manager, String service) throws IOException {

        }
    }

}
