package com.vojavy.AlmAgoraHub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stag")
public class StagProperties {
    private String loginPath = "/ws/login";
    private String wsBaseUrlFirst;
    private String wsBaseUrlSecond;
    private String callbackUrl;

    public String getLoginPath() { return loginPath; }
    public void setLoginPath(String loginPath) { this.loginPath = loginPath; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public String getWsBaseUrlFirst() { return wsBaseUrlFirst; }
    public void setWsBaseUrlFirst(String wsBaseUrlFirst) {
        this.wsBaseUrlFirst = wsBaseUrlFirst;
    }

    public String getWsBaseUrlSecond() { return wsBaseUrlSecond; }
    public void setWsBaseUrlSecond(String wsBaseUrlSecond) {
        this.wsBaseUrlSecond = wsBaseUrlSecond;
    }
}
