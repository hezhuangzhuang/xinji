package com.zxwl.xinji.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.xinji.R;

/**
 * author：pc-20171125
 * data:2019/5/17 13:19
 */
public class CustomInputDialog extends Dialog {
    private TextView tvCancle;
    private TextView tvSend;
    private ContainsEmojiEditText etComment;

    private String hint;

    private int contentLength = 500;

    public CustomInputDialog(@NonNull Context context) {
        this(context, 0);
    }

    public CustomInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public CustomInputDialog(@NonNull Context context, int themeResId, String hint) {
        super(context, themeResId);
        this.hint = hint;
    }

    public CustomInputDialog(@NonNull Context context, int themeResId, String hint, int contentLength) {
        super(context, themeResId);
        this.hint = hint;
        this.contentLength = contentLength;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_layout);

        tvCancle = (TextView) findViewById(R.id.tv_cancle);
        tvSend = (TextView) findViewById(R.id.tv_send);
        etComment = (ContainsEmojiEditText) findViewById(R.id.et_comment);
//        etComment = (EditText) findViewById(R.id.et_comment);

        //设置输入的长度
        setContentLength(contentLength);

        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);//dialog底部弹出
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        if (!TextUtils.isEmpty(hint)) {
            etComment.setHint(hint);
        }

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = etComment.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastHelper.showShort("回复不能为空");
                    return;
                }
                if (null != clickListener) {
                    clickListener.sendComment(trim);
                }
            }
        });
    }

    public onSendClickListener clickListener;

    public void setClickListener(onSendClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void clearContent() {
        etComment.setText("");
    }

    /**
     * 发送按钮点击事件回调
     */
    public interface onSendClickListener {
        public void sendComment(String comment);
    }

    /**
     * 设置输入框的长度
     *
     * @param maxLength
     */
    public void setContentLength(int maxLength) {
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        etComment.setFilters(filters);
    }
}
