package com.rockbb.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sms")
public class SysConfig {
    private int inDevelopment;
    private String buildTimestamp;
    private String cookiePrefix;
    private String encoding;
    private String serverEncoding;
    private String siteBase;
    private String rootPath;
    private String resourcePath;

    public int getInDevelopment() {
        return inDevelopment;
    }

    public void setInDevelopment(int inDevelopment) {
        this.inDevelopment = inDevelopment;
    }

    public String getBuildTimestamp() {
        return buildTimestamp;
    }

    public void setBuildTimestamp(String buildTimestamp) {
        this.buildTimestamp = buildTimestamp;
    }

    public String getCookiePrefix() {
        return cookiePrefix;
    }

    public void setCookiePrefix(String cookiePrefix) {
        this.cookiePrefix = cookiePrefix;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getServerEncoding() {
        return serverEncoding;
    }

    public void setServerEncoding(String serverEncoding) {
        this.serverEncoding = serverEncoding;
    }

    public String getSiteBase() {
        return siteBase;
    }

    public void setSiteBase(String siteBase) {
        this.siteBase = siteBase;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
