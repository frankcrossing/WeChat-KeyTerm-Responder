package com.blanke.wechatbotxposed.Mysql;

import java.util.Date;

public class Data {

    private String content;

    private Date createTime;

    private String key;

    public Data(String c, Date t, String k) {
        content = c;
        createTime = t;
        key = k;
    }

    public void setContent(String c) {
        content = c;
    }

    public String getContent() {
        return content;
    }

    public void setTime(Date t){
        createTime = t;
    }

    public Date getTime(){
        return createTime;
    }

    public void setKey(String k){
        key = k;
    }

    public String getKey(){
        return key;
    }
}
