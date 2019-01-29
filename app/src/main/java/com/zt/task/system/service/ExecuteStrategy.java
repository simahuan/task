package com.zt.task.system.service;

/**
 * @author
 */
public interface ExecuteStrategy {

    /**
     * 应用刷词
     */
    int TYPE_BRUSH_WORD = 0x1;
    /**
     * 应用下载
     */
    int TYPE_APP_DOWNLOAD = 0x2;
    /**
     * 应用评论
     */
    int TYPE_COMMENT = 0x3;

    /**
     * 执行类型
     * @param type
     */
    void executeType(int type);
}
