package jianrt.slidetounlock.entity;

import java.util.List;

/**
 * Author:11719<p>
 * CreateDate:2017/3/2<p>
 * Fuction:<p>
 */

public class AppTrustList {
    private List<String> list;
    /**
     * password : 1234
     */

    private String password;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
