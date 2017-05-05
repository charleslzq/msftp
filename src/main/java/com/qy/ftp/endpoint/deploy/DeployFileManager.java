package com.qy.ftp.endpoint.deploy;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by liuzhengqi on 4/24/2017.
 */
public class DeployFileManager {
    private final String basePath;

    public DeployFileManager(String basePath) {
        this.basePath = basePath;
    }

    public Path getPath(final String path) {
        return Paths.get(basePath, path);
    }

    public List<File> listFile(String path, FileFilter filter) {
        Path filePath = getPath(path);
        if (filePath.toFile().exists()) {
            if (filePath.toFile().isFile()) {
                return Collections.singletonList(filePath.toFile());
            } else {
                return Arrays.asList(filePath.toFile().listFiles(
                        filter == null ? (pathName -> true) : filter
                ));
            }
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
