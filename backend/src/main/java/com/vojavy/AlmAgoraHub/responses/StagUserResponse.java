package com.vojavy.AlmAgoraHub.responses;

import java.util.List;

public class StagUserResponse {

    private List<StagUserInfo> stagUserInfo;

    public List<StagUserInfo> getStagUserInfo() {
        return stagUserInfo;
    }

    public void setStagUserInfo(List<StagUserInfo> stagUserInfo) {
        this.stagUserInfo = stagUserInfo;
    }

    public static class StagUserInfo {
        private String osCislo;

        public String getOsCislo() {
            return osCislo;
        }

        public void setOsCislo(String osCislo) {
            this.osCislo = osCislo;
        }
    }
}
