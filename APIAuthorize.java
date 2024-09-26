package com.smartcity;

public class APIAuthorize {
    //這邊請替換為申請的授權碼
    private final String Authorization = "CWA-AA44B18B-3CDE-43BE-9A61-95B5F9FECCE5"; // 授權碼
                                       // CWA-AA44B18B-3CDE-43BE-9A61-95B5F9FECCE5
    //向外提供授權碼
    public String getAuthorization() {
        return Authorization;   // 取出認證碼
    }
}