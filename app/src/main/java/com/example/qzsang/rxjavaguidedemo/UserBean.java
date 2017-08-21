package com.example.qzsang.rxjavaguidedemo;

/**
 * Created by qzsang on 2017/8/21.
 */

public class UserBean {
    public String name;

    public UserBean(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
