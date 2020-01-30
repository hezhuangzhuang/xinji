package com.zxwl.frame.utils;

import android.app.Activity;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器逻辑
 */
public class TimerCount {
    private int count = 0;//秒数计数器
    private TextView container;
    private Timer timer;
    private TimerTask task;
    private Activity activity;

    public TimerCount(Activity activity){
        this.activity = activity;
    }

    /**
     * 配置计数器初始值
     * @param count
     */
    public void setCount(int count){
        this.count = count;
    }

    /**
     * 设置用于显示的容器
     * @param container
     */
    public void setContainer(TextView container){
        this.container = container;
    }

    /**
     * 启动计时器
     */
    public void start(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                count = count + 1;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        container.setText(timerFormat(count));
                    }
                });
            }
        };
        timer.schedule(task,0,1000);
    }

    /**
     * 结束计时器
     */
    public void end(){
        timer.cancel();
        task.cancel();
        timer = null;
        task = null;
        activity = null;
        count = 0;
    }

    /**
     * 格式化日期
     * @param count
     * @return
     */
    private String timerFormat(int count){
        StringBuilder h = new StringBuilder();
        StringBuilder m = new StringBuilder();
        StringBuilder s = new StringBuilder();

        h.append(count/3600);
        if (h.length() == 1){
            h.insert(0,0);
        }

        m.append(count/60-(count/3600)*60);
        if (m.length() == 1){
            m.insert(0,0);
        }

        s.append(count%60);
        if (s.length() == 1){
            s.insert(0,0);
        }

        return h.append(":").append(m).append(":").append(s).toString();
    }
}
