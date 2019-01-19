package com.zt.task.system.util;

/**
 * @author ytf
 */
public class Constant {
    public static final String URL = "https://api.douban.com/";
    public static final String URLPATH = "v2/movie/top250";//?start=0&count=10
    public static final String BaseURL = "https://api.douban.com/v2/movie/top250?start=0&count=10";//?start=0&count=10

    public static final String KEY_TASK_BEAN_KEYWORDS = "key_keywords";
    public static final String KEY_TASK_BEAN_COUNT = "key_count";

    public static final String KEY_TASK_BEAN = "key_task_bean";
    public static final String KEY_COMMAND_BEAN = "key_command_bean";

    public static final String KEY_TASK_TYPE = "key_task_type";

    public static final String KEY_TASK_STATUS = "key_task_status";

    public static final String KEY_TASK_INIT_NOT_START = "key_task_init_not_start";

    public static final String KEY_TASK_CREATE_TIME = "key_task_create_time";
    public static final String KEY_TASK_EXECUTE_STATISTICAL = "key_task_execute_statistical";
    public static final String KEY_TASK_SPENT_TIME = "key_task_spent_time";

    public static final int TASK_IDLE = 0;
    public static final int TASK_EXECUTE = 1;
    public static final int TASK_CANCEL = 2;
    public static final int TASK_COMPLETED = 3;
    public static final int TASK_ERROR = -1;


}
