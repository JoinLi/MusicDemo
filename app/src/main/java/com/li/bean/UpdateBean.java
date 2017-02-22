package com.li.bean;

import java.util.List;

public class UpdateBean {


    /**
     * rs_code : 1000
     * totalRecordCount : 1
     * mydata : [{"versionCode":2,"versionName":"1.2","bbmingcheng":"修复已知bug，新增友盟sdk","dizhi":"http://jk.webbiao.com/cxb/201728554179412.apk"}]
     */

    private String rs_code;
    private String totalRecordCount;
    /**
     * versionCode : 2
     * versionName : 1.2
     * bbmingcheng : 修复已知bug，新增友盟sdk
     * dizhi : http://jk.webbiao.com/cxb/201728554179412.apk
     */

    private List<MydataBean> mydata;

    public String getRs_code() {
        return rs_code;
    }

    public void setRs_code(String rs_code) {
        this.rs_code = rs_code;
    }

    public String getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(String totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public List<MydataBean> getMydata() {
        return mydata;
    }

    public void setMydata(List<MydataBean> mydata) {
        this.mydata = mydata;
    }

    public static class MydataBean {
        private int versionCode;
        private String versionName;
        private String bbmingcheng;
        private String dizhi;

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getBbmingcheng() {
            return bbmingcheng;
        }

        public void setBbmingcheng(String bbmingcheng) {
            this.bbmingcheng = bbmingcheng;
        }

        public String getDizhi() {
            return dizhi;
        }

        public void setDizhi(String dizhi) {
            this.dizhi = dizhi;
        }
    }
}