package com.qy.ftp.endpoint.deploy;

import com.qy.ftp.support.TreeCopier;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by liuzhengqi on 4/24/2017.
 */
public class DeployFileMvcEndpoint extends EndpointMvcAdapter {

    public DeployFileMvcEndpoint(DeployFileEndpoint delegate) {
        super(delegate);
    }

    public Path getPath(final String path) {
        return ((DeployFileEndpoint) getDelegate()).getDeployFileManager().getPath(path);
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    @ResponseBody
    public List<File> list(
            HttpServletRequest request,
            @RequestParam(value = "accept", required = false, defaultValue = ".*") final String regex
    ) {
        String path = extractRelativePath(request);
        FileFilter fileFilter = regex == null ?
                file -> true :
                file -> Pattern.compile(regex).matcher(file.getName()).find();
        return ((DeployFileEndpoint) getDelegate()).getDeployFileManager().listFile(path, fileFilter);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ResponseBody
    public CopyResponse copyTo(
            @RequestParam("source") final List<String> sourceList,
            @RequestParam("dest") final List<String> destList
    ) {
        String source = sourceList.stream().collect(Collectors.joining(File.separator));
        String dest = destList.stream().collect(Collectors.joining(File.separator));
        Path sourcePath = getPath(source);
        if (!sourcePath.toFile().exists()) {
            return new CopyResponse(false, sourcePath.toString(), dest, "Source File Not Exist!");
        }
        try {
            Path destPath = Paths.get(dest);
            if (!destPath.toFile().exists() || destPath.toFile().isFile()) {
                destPath.toFile().mkdirs();
            }

            Files.walkFileTree(sourcePath, new TreeCopier(sourcePath, destPath));
            return new CopyResponse(true, sourcePath.toString(), dest, "File(s) copped successfully!");
        } catch (Exception e) {
            return new CopyResponse(false, sourcePath.toString(), dest, "Error Copying File:\t" + e.getMessage());
        }
    }

    private String extractRelativePath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    @Data
    @AllArgsConstructor
    static class CopyResponse {
        private boolean success;
        private String source;
        private String destDir;
        private String message;
    }
}
