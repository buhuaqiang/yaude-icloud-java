package com.yaude.icloud.openstack.utils;

import org.springframework.stereotype.Component;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName PortNameUtil.java
 * @Description TODO
 * @createTime 2021年10月19日 11:50:00
 */
public  class PortNameUtil {

    public static String getNameByPort(int port){
        String name ="";
        switch (port){
            case 22:
                name = "(SSH)";
                break;
            case 80:
                name ="(HTTP)";
                break;
            default:
                name = "";

        }
        return name;
    }
}
