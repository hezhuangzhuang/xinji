package com.zxwl.xinji.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.zxwl.xinji.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author：pc-20171125
 * data:2019/6/14 10:24
 */
public class ServiceHelper {
    /**
     * 判断某个service是否正在运行
     *
     * @param context
     * @param runService 要验证的service组件的类名
     * @return
     */
    public static boolean isServiceRunning(Context context,
                                           Class<? extends Service> runService) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) am
                .getRunningServices(1024);
        for (int i = 0; i < runningService.size(); ++i) {
            if (runService.getName().equals(
                    runningService.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回app运行状态
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return int 1:前台 2:后台 0:不存在
     */
    public static int isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> listInfos = activityManager.getRunningTasks(20);
        // 判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return 2;
                }
            }
            return 0;// 栈里找不到，返回3
        }
    }

    /**
     * 自动判断appUI进程是否已在运行，设置跳转信息
     *
     * @param context
     */
    public static void startActivityWithAppIsRuning(Context context,
//                                                    Intent intent,
                                                    String packageName) {
        int isAppRuning = isAppAlive(context, context.getPackageName());
        if (isAppRuning != 0) {
            Intent newIntent = new Intent(context, MainActivity.class);
//            newIntent.putExtras(intent.getExtras());
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
            return;
        }
        // 如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
        // SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入
        // 参数跳转到DetailActivity中去了
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launchIntent.setPackage(null); // 加上这句代码
        context.startActivity(launchIntent);
    }

    /**
     * 启动App时，为跳转到主页MainActivity的Intent写入打开通知的Intent，如果有通知的情况下
     *
     * @param appStartActivity        app启动的第一个activity，在配置文件中设置的mainactivity
     * @param startMainActivityIntent
     */
    public static void startAppMainActivitySetNoticeIntent(
            Activity appStartActivity,
            Intent startMainActivityIntent) {
//        /**
//         * 如果启动app的Intent中带有额外的参数，表明app是从点击通知栏的动作中启动的 将参数取出，传递到MainActivity中
//         */
//        try {
//            if (appStartActivity.getIntent().getExtras() != null) {
//                if (appStartActivity.getIntent().getExtras()
//                        .getBoolean(UmenPushManager.FORM_NOTICE_OPEN) == true) {
//                    startMainActivityIntent
//                            .putExtra(
//                                    UmenPushManager.FORM_NOTICE_OPEN_DATA,
//                                    appStartActivity
//                                            .getIntent()
//                                            .getExtras()
//                                            .getParcelable(
//                                                    UmenPushManager.FORM_NOTICE_OPEN_DATA));
//                }
//            }
//        } catch (Exception e) {
//
//        }
    }

    /**
     * 判断是否是点击消息通知栏跳转过来的
     *
     * @param mainActivity 主页
     */
    public static void isAppWithNoticeOpen(Activity mainActivity) {
//        try {
//            if (mainActivity.getIntent().getExtras() != null) {
//                Intent intent = mainActivity.getIntent().getExtras().getParcelable(UmenPushManager.FORM_NOTICE_OPEN_DATA);
//                Intent newIntent = new Intent(mainActivity, (Class<?>) intent.getExtras().getSerializable(UmenPushManager.CALL_TO_ACTIVITY));
//                newIntent.putExtras(intent.getExtras());
//                mainActivity.startActivity(newIntent);
//            }
//        } catch (Exception e) {
//
//        }
    }

    /**
     * 判断进程是否运行
     *
     * @param context
     * @param proessName
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 判断某一个类是否存在任务栈里面
     *
     * @return
     */
    public static boolean isExsitMianActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context
                .getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break; // 跳出循环，优化效率
                }
            }
        }
        return flag;
    }
}
