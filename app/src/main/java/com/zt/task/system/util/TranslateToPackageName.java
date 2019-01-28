package com.zt.task.system.util;

/**
 * @author
 */
public class TranslateToPackageName {


    public static String translateToPackageName(String appMarket) {
        if ("百度".equalsIgnoreCase(appMarket)) {
            return "com.baidu.appsearch";
        } else if ("App Store".equalsIgnoreCase(appMarket)) {
            throw new RuntimeException("App Store 未开发");
        } else {
            return "com.baidu.appsearch";
        }
    }
}
