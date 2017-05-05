package com.qy.ftp.user;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Created by liuzhengqi on 4/21/2017.
 */
public class YmlUserManager extends AbstractUserManager {
    private final Map<String, User> userMap;

    public YmlUserManager(Map<String, User> userMap, String adminName) {
        this(userMap, adminName, null);
    }

    public YmlUserManager(Map<String, User> userMap, String adminName, PasswordEncryptor passwordEncryptor) {
        super(adminName, passwordEncryptor == null ? new ClearTextPasswordEncryptor() : passwordEncryptor);
        Assert.notEmpty(userMap, "Should have at least one user");
        Assert.hasText(adminName, "Admin should be specified");
        Assert.isTrue(userMap.containsKey(adminName), "Admin not included in user configurations");
        this.userMap = userMap;
    }

    @Override
    public User getUserByName(String name) {
        return userMap.get(name);
    }

    @Override
    public String[] getAllUserNames() {
        return userMap.keySet().stream().toArray(String[]::new);
    }

    @Override
    public void delete(String name) {
        userMap.remove(name);
    }

    @Override
    public void save(User user) throws FtpException {
        throw new FtpException("Unsupported operation");
    }

    @Override
    public boolean doesExist(String name) {
        return userMap.containsKey(name);
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication auth = (UsernamePasswordAuthentication) authentication;
            String user = auth.getUsername();
            String password = auth.getPassword();
            if (user == null) {
                throw new AuthenticationFailedException("Authentication failed");
            } else {
                if (password == null) {
                    password = "";
                }

                String storedPassword = this.userMap.get(user).getPassword();
                if (storedPassword == null) {
                    throw new AuthenticationFailedException("Authentication failed");
                } else if (this.getPasswordEncryptor().matches(password, storedPassword)) {
                    return this.getUserByName(user);
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }
            }
        } else if (authentication instanceof AnonymousAuthentication) {
            if (this.doesExist("anonymous")) {
                return this.getUserByName("anonymous");
            } else {
                throw new AuthenticationFailedException("Authentication failed");
            }
        } else {
            throw new IllegalArgumentException("Authentication not supported by this user manager");
        }
    }
}
