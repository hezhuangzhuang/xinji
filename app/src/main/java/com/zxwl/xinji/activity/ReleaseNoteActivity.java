package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.NoteBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

/**
 * 发布或修改记事本
 */
public class ReleaseNoteActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private TextView tvTitleLable;
    private ContainsEmojiEditText etTitle;
    private View viewOne;
    private TextView tvTime;
    private TextView tvContentLable;
    private ContainsEmojiEditText etContent;
    private FrameLayout flDel;

    private NoteBean noteBean;
    public static final String NOTE_BEAN = "NOTE_BEAN";

    public static void startActivity(Context context, NoteBean noteBean) {
        Intent intent = new Intent(context, ReleaseNoteActivity.class);
        intent.putExtra(NOTE_BEAN, noteBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        tvTitleLable = (TextView) findViewById(R.id.tv_title_lable);
        etTitle = (ContainsEmojiEditText) findViewById(R.id.et_title);
        viewOne = (View) findViewById(R.id.view_one);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvContentLable = (TextView) findViewById(R.id.tv_content_lable);
        etContent = (ContainsEmojiEditText) findViewById(R.id.et_content);
        flDel = (FrameLayout) findViewById(R.id.fl_del);
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("详情");

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvRightOperate.setText("保存");

        noteBean = (NoteBean) getIntent().getSerializableExtra(NOTE_BEAN);

        if (null != noteBean) {
            etTitle.setText(noteBean.title);
            etContent.setText(noteBean.context);
            tvTime.setText(DateUtil.longToString(noteBean.createTime, DateUtil.FORMAT_DATE_TIME));

            flDel.setVisibility(View.VISIBLE);
        } else {
            tvTime.setVisibility(View.GONE);
            tvTitleLable.setVisibility(View.VISIBLE);
            tvContentLable.setVisibility(View.VISIBLE);
            viewOne.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        flDel.setOnClickListener(this);
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != noteBean) {
                    flDel.setVisibility(View.VISIBLE);
                }
                tvRightOperate.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != noteBean) {
                    flDel.setVisibility(View.VISIBLE);
                }
                tvRightOperate.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release_note;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;


            case R.id.tv_right_operate:
                String title = etTitle.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    ToastHelper.showShort("标题不能为空");
                    return;
                }

                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastHelper.showShort("内容不能为空");
                    return;
                }

                updateNote(title, content);
                break;

            case R.id.fl_del:
                showDelDialog();
                break;

        }
    }

    /**
     * 更新笔记
     *
     * @param title
     * @param content
     */
    private void updateNote(String title, String content) {

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .updateNote(getRequestBody(title, content))
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));

                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("更新异常" + responeThrowable.message);
                    }
                });
    }

    /**
     * 获取请求体
     */
    @NonNull
    private RequestBody getRequestBody(String title, String content) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("context", content);

            if (null != noteBean) {
                jsonObject.put("id", noteBean.id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    private MaterialDialog delDialog;

    /**
     * 显示对话框
     */
    private void showDelDialog() {
        if (null == delDialog) {
            initDelDialog();
        }
        //点击对话框以外的地方和返回键，对话框都不消失
        //callDialog.setCancelable(false);
        delDialog.show();
    }

    private void initDelDialog() {
        View inflate = View.inflate(this, R.layout.dialog_call, null);
        TextView tvNumber = inflate.findViewById(R.id.tv_title);
        TextView tvCall = inflate.findViewById(R.id.tv_ok);
        TextView tvCancle = inflate.findViewById(R.id.tv_cancle);

        tvNumber.setText("您确定要删除该笔记吗?");
        tvNumber.setTextColor(ContextCompat.getColor(this, R.color.color_E42417));
        tvCall.setText("确认");
        tvCancle.setText("取消");

        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != delDialog) {
                    delDialog.dismiss();
                }
                delNote();
            }
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != delDialog) {
                    delDialog.dismiss();
                }
            }
        });

        delDialog = new MaterialDialog.Builder(this)
                .customView(inflate, false)
                .build();
        //点击对话框以外的地方，对话框不消失
        delDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 删除笔记
     */
    private void delNote() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .delNote(String.valueOf(noteBean.id))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();

                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));

                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }


}
