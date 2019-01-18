package com.zt.task.system.acvitity;

import android.os.Bundle;

import com.zt.task.system.R;
import com.zt.task.system.service.BaseAccessibilityService;
import com.zt.task.system.util.Constant;
import com.zt.task.system.util.Preferences;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author ytf
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        BaseAccessibilityService.getInstance().init(this);
        /**
         * 初始化任务 TASK_IDLE
         */
        Preferences.set(getBaseContext(), Constant.KEY_TASK_STATUS,Constant.TASK_IDLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
