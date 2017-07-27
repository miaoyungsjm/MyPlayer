package my.com;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by MY on 2017/7/27.
 *
 * 继承 Activity ，作为其他 Activity 的父类
 * 在 LogCat 打印对应 Activity 活动的类名
 * 通过 ActivityCollector.activityList 统一管理所有 Activity ，实现随时退出程序 ActivityCollector.finishAll()
 */

public class BaseActivity extends Activity{

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 在 LogCat 打印对应 Activity 活动的类名
        Log.d(TAG,getClass().getSimpleName());
        // 在 ActivityCollector.activityList 添加 Activity
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 在 ActivityCollector.activityList 移除 Activity
        ActivityCollector.removeActivity(this);
    }
}
