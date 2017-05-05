package com.qy.ftp.support;

import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
@RestController
public class ServerStatusController {

    @RequestMapping(value = "/serverisok", method = RequestMethod.GET)
    public Health serverIsOK() {
        return new Health.Builder().status("ok").build();
    }
}
