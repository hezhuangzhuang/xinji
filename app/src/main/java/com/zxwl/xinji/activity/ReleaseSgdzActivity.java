package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
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
 * 设岗定责
 */
public class ReleaseSgdzActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private ContainsEmojiEditText etName;
    private TextView tvGwsdLable;
    private TextView tvGwsd;
    private View viewTwo;
    private TextView tvLxhLable;
    private ContainsEmojiEditText etLxh;
    private TextView tvRemarksLable;
    private ContainsEmojiEditText etRemarks;

    public static final String TITLE = "TITLE";
    private String title;

    public static final String TYPE_SGDZ = "设岗定责";
    public static final String TYPE_DYLXH = "党员联系户";

    private String name;
    private String lxhmd;
    private String remarks;

    //岗位设定
    private int gwsdMode=-1;
    private String requestUrl;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, ReleaseSgdzActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = findViewById(R.id.iv_back_operate);
        tvTopTitle = findViewById(R.id.tv_top_title);
        tvRightOperate = findViewById(R.id.tv_right_operate);
        etName = findViewById(R.id.et_name);
        tvGwsdLable = findViewById(R.id.tv_gwsd_lable);
        tvGwsd = findViewById(R.id.tv_gwsd);
        viewTwo = findViewById(R.id.view_two);
        tvLxhLable = findViewById(R.id.tv_lxh_lable);
        etLxh = findViewById(R.id.et_lxh);
        tvRemarksLable = findViewById(R.id.tv_remarks_lable);
        etRemarks = findViewById(R.id.et_remarks);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);

        tvTopTitle.setText(title);
        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

        switch (title) {
            //党员联系户
            case TYPE_DYLXH:
                tvGwsdLable.setVisibility(View.GONE);
                tvGwsd.setVisibility(View.GONE);
                viewTwo.setVisibility(View.GONE);

                setContentLength(etLxh, 2000);

                requestUrl = Urls.ADD_DYLXH;
                break;

            //设岗定责
            case TYPE_SGDZ:
                tvLxhLable.setText("优势特长");
                etLxh.setHint("请输入优势特长");

                requestUrl = Urls.ADD_SGDZ;
                break;
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        tvGwsd.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release_sgdz;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                name = etName.getText().toString().trim();
                if (checkText(name, "姓名")) {
                    return;
                }

                lxhmd = etLxh.getText().toString().trim();

                if (checkText(lxhmd, TYPE_DYLXH.equals(title) ? "联系户名单" : "优势特长")) {
                    return;
                }

                remarks = etRemarks.getText().toString().trim();

                if (TYPE_SGDZ.equals(title) && gwsdMode == -1) {
                    ToastHelper.showShort("请选择岗位设定");
                    return;
                }

                addRequest();
                break;

            case R.id.tv_gwsd:
                showGwsdSelectDialog(types, gwsdMode-1);
                break;
        }
    }

    /**
     * 添加的网络请求
     */
    private void addRequest() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addSgdz(requestUrl, getRequestBody())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort("添加成功");
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("请求失败,error" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 判断输入内容是否为空
     *
     * @param content
     * @param hint
     * @return
     */
    public boolean checkText(String content, String hint) {
        if (TextUtils.isEmpty(content)) {
            ToastHelper.showShort(hint + "不能为空");
            return true;
        }
        return false;
    }

    /**
     * 获取请求体
     */
    @NonNull
    private RequestBody getRequestBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("remark", remarks);

            if (TYPE_DYLXH.equals(title)) {
                jsonObject.put("cname", lxhmd);
            } else {
                jsonObject.put("job", gwsdMode);
                jsonObject.put("specialty", lxhmd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    private void setContentLength(EditText etContent, int maxLength) {
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        etContent.setFilters(filters);
    }

    private MaterialDialog confTypeDialog;

    private String[] types = {
            "政策宣传",
            "文明示范",
            "矛盾调解",
            "治安巡逻",
            "环境整治",
            "便民服务",
            "致富带动",
            "计生服务",
            "村务监督",
            "党务监督",
            "其他"
    };

    /**
     * 会议类型选择
     */
    private void showGwsdSelectDialog(String[] confTypes, int selectedIndex) {
        confTypeDialog = new MaterialDialog.Builder(this)
                .title("请选择岗位")
                .items(confTypes)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        tvGwsd.setText(confTypes[which]);
                        gwsdMode = (which + 1);
                        return true;
                    }
                })
                .build();
        //点击对话框以外的地方，对话框不消失
//        confTypeDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //dialog.setCancelable(false);
        confTypeDialog.show();
    }
}
