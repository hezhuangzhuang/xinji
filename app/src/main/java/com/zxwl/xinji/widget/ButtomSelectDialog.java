package com.zxwl.xinji.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zxwl.xinji.R;


/**
 * author：pc-20171125
 * data:2019/5/17 13:19
 */
public class ButtomSelectDialog extends Dialog {
    private TextView tvPicture;
    private TextView tvAlbum;
    private TextView tvCancle;

    public static final int TYPE_PICTURE = 0;
    public static final int TYPE_ALBUM = 1;

    public ButtomSelectDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ButtomSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_head);
        tvPicture = (TextView) findViewById(R.id.tv_picture);
        tvAlbum = (TextView) findViewById(R.id.tv_album);
        tvCancle = (TextView) findViewById(R.id.tv_cancle);

        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);//dialog底部弹出
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickListener) {
                    clickListener.selectClick(TYPE_PICTURE);
                }
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickListener) {
                    clickListener.selectClick(TYPE_ALBUM);
                }
            }
        });
    }

    public onItemClickListener clickListener;

    public void setClickListener(onItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * 按钮点击事件
     */
    public interface onItemClickListener {
        public void selectClick(int type);
    }
}
