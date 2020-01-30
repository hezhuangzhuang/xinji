package com.zxwl.xinji.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxwl.xinji.R;


/**
 * author：pc-20171125
 * data:2019/7/31 17:47
 */
public class StartView extends LinearLayout {
    public StartView(Context context) {
        super(context);
        initView(context);
    }

    public StartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        Log.i("StartView", "初始化StartView");
        View inflate = View.inflate(context, R.layout.item_start, null);
        TextView tvStart = null;
        for (int i = 0; i < 5; i++) {
            tvStart = (TextView) View.inflate(context, R.layout.item_start, null);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            tvStart.setLayoutParams(layoutParams);
            tvStart.setText((i + 1) * 2 + "分");
            tvStart.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_start_false, 0, 0);

            int finalI = i;
            tvStart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStartStatus(finalI);
                }
            });
            addView(tvStart);
        }
    }

    private void setStartStatus(int finalI) {
        setStatusFlase();
        int childCount = getChildCount();
        for (int i = 0; i <= finalI; i++) {
            TextView tvStart = (TextView) getChildAt(i);
            tvStart.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ff9d04));
            tvStart.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_start_true, 0, 0);
        }

        if (null != startClickListener) {
            startClickListener.onClick((finalI + 1) * 2);
        }
    }

    private void setStatusFlase() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView tvStart = (TextView) getChildAt(i);
            tvStart.setTextColor(ContextCompat.getColor(getContext(), R.color.color_aaa));
            tvStart.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_start_false, 0, 0);
        }
    }

    public interface onStartClickListener {
        void onClick(int socre);
    }

    private onStartClickListener startClickListener;

    public void setStartClickListener(onStartClickListener startClickListener) {
        this.startClickListener = startClickListener;
    }
}
