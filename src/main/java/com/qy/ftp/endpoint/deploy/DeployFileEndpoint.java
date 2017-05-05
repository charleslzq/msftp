package com.qy.ftp.endpoint.deploy;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.io.File;
import java.util.List;

/**
 * Created by liuzhengqi on 4/24/2017.
 */
public class DeployFileEndpoint extends AbstractEndpoint<List<File>> {
    @Getter(AccessLevel.PROTECTED)
    private final DeployFileManager deployFileManager;

    public DeployFileEndpoint(String id, DeployFileManager deployFileManager) {
        super(id);
        this.deployFileManager = deployFileManager;
    }

    public DeployFileEndpoint(String id, boolean sensitive, DeployFileManager deployFileManager) {
        super(id, sensitive);
        this.deployFileManager = deployFileManager;
    }

    public DeployFileEndpoint(String id, boolean sensitive, boolean enabled, DeployFileManager deployFileManager) {
        super(id, sensitive, enabled);
        this.deployFileManager = deployFileManager;
    }

    @Override
    public List<File> invoke() {
        return deployFileManager.listFile(".", null);
    }
}
