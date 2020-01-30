package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.AdvisoryBean;
import com.zxwl.network.bean.response.ChatBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NewAdvisoryBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.ChatAdapter;
import com.zxwl.xinji.adapter.item.ChatItem;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 咨询详情界面
 */
public class ConsultationDetailsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvSend;
    private RecyclerView rvList;
    private ContainsEmojiEditText etContent;

    private ChatAdapter chatAdapter;

    public static final String IS_ADD = "IS_ADD";

    public static final String SERIAL_NO = "SERIAL_NO";

    //是否是新增我要咨询
    private boolean isAdd = false;

    //是否是第一条消息
    private boolean isFirst = true;

    //咨询编号
    private int serialNo;

    private int pageNum = 0;
    private int PAGE_SIZE = 100;

    private View errorView;

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context, boolean isAdd) {
        Intent intent = new Intent(context, ConsultationDetailsActivity.class);
        intent.putExtra(IS_ADD, isAdd);
        intent.putExtra(SERIAL_NO, -1);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int serialNo) {
        Intent intent = new Intent(context, ConsultationDetailsActivity.class);
        intent.putExtra(SERIAL_NO, serialNo);
        intent.putExtra(IS_ADD, false);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvSend = (TextView) findViewById(R.id.tv_send);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        etContent = (ContainsEmojiEditText) findViewById(R.id.etContent);

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("咨询内容");

        accountBean = PreferenceUtil.getUserInfo(this);

        isAdd = getIntent().getBooleanExtra(IS_ADD, false);

        serialNo = getIntent().getIntExtra(SERIAL_NO, -1);

        initRecycler(new ArrayList<>());

        //如果不是添加获取我要咨询的数据
        if (!isAdd) {
            //获取我要咨询数据
            showSkeletonSceen();

            queryWyzxDetail();
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvSend.setOnClickListener(this);

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    tvSend.setTextColor(ContextCompat.getColor(getApplication(),R.color.color_333));
                    tvSend.setClickable(true);
                } else {
                    tvSend.setTextColor(ContextCompat.getColor(getApplication(),R.color.color_999));
                    tvSend.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_consultation_details;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_send:
                String content = etContent.getText().toString().trim();

                if(TextUtils.isEmpty(content)){
                    ToastHelper.showShort("咨询内容不能为空");
                    break;
                }

                DialogUtils.showProgressDialog(ConsultationDetailsActivity.this, "正在发送咨询内容...");

                //判断是否是新增
                if (isAdd) {
                    //如果是第一条消息，新增
                    if (isFirst) {
                        newAdvisoryRequest(StringUtils.encoder(content));
                    } else {
                        //追加咨询
                        appendAdvisoryRequest(StringUtils.encoder(content));
                    }
                } else {
                    //追加咨询
                    appendAdvisoryRequest(StringUtils.encoder(content));
                }
                break;
        }
    }

    private void initRecycler(List<ChatItem> data) {
        chatAdapter = new ChatAdapter(0, data);

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(chatAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chatAdapter.getData().size() > 1) {
                    rvList.smoothScrollToPosition(chatAdapter.getData().size() - 1);
                }
            }
        }, 500);
    }

    private void addMessageAdapter(ChatBean chatBean) {
        etContent.setText("");
        ChatItem chatItem = new ChatItem(chatBean);
        chatAdapter.getData().add(chatItem);
        chatAdapter.notifyDataSetChanged();
        rvList.smoothScrollToPosition(chatAdapter.getData().size() - 1);
    }

    /**
     * 查询我要咨询详情
     */
    private void queryWyzxDetail() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryWyzxDetail(pageNum, PAGE_SIZE, serialNo)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<ChatBean>>() {
                    @Override
                    public void onSuccess(BaseData<ChatBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            //消息
                            List<ChatBean> chatBeans = baseData.dataList;

                            Collections.reverse(chatBeans);

                            //消息不为空
                            if (chatBeans.size() > 0) {
                                List<ChatItem> newItems = new ArrayList<>();

                                for (int i = 0; i < chatBeans.size(); i++) {
                                    newItems.add(new ChatItem(chatBeans.get(i)));
                                }

                                chatAdapter.setNewData(newItems);

                                rvList.smoothScrollToPosition(chatAdapter.getData().size() - 1);
                            }
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                        hideSkeletonScreen();
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        hideSkeletonScreen();

                        chatAdapter.setEmptyView(errorView);
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 新增咨询
     */
    private void newAdvisoryRequest(String content) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .newAdvisory(Integer.valueOf(accountBean.id), content)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<NewAdvisoryBean>() {
                    @Override
                    public void onSuccess(NewAdvisoryBean bean) {

                        DialogUtils.dismissProgressDialog();

                        if (BaseData.SUCCESS.equals(bean.result)) {
                            serialNo = bean.serialNo;
                            ChatBean chatBean = new ChatBean();
                            chatBean.content = etContent.getText().toString().trim();
                            chatBean.sendDate = System.currentTimeMillis();
                            chatBean.senderName = accountBean.name;
                            chatBean.type = 0;
                            addMessageAdapter(chatBean);

                            //设置为不是首次发送
                            isFirst = false;
                        } else {
                            ToastHelper.showShort(bean.message);
                        }

                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("发送失败,请重新发送");
//                        ToastHelper.showShort("onError" + responeThrowable.getCause().toString());
                    }
                });

    }

    /**
     * 追加咨询
     */
    private void appendAdvisoryRequest(String content) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .appendAdvisory(Integer.valueOf(accountBean.id), content, serialNo)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<NewAdvisoryBean>() {
                    @Override
                    public void onSuccess(NewAdvisoryBean bean) {

                        DialogUtils.dismissProgressDialog();

                        if (BaseData.SUCCESS.equals(bean.result)) {
                            serialNo = bean.serialNo;

                            ChatBean chatBean = new ChatBean();
                            chatBean.content = etContent.getText().toString().trim();
                            chatBean.sendDate = System.currentTimeMillis();
                            chatBean.senderName = accountBean.name;
                            chatBean.type = 0;
                            addMessageAdapter(chatBean);
                        } else {
                            ToastHelper.showShort(bean.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("发送失败,请重新发送");

//                        ToastHelper.showShort("onError" + responeThrowable.getCause().toString());
                    }
                });
    }

    private SkeletonScreen skeletonScreen;

    /**
     * 显示骨架图
     */
    private void showSkeletonSceen() {
        skeletonScreen = Skeleton.bind(rvList)
                .adapter(chatAdapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.item_skeleton_news)
                .show(); //default count is 10
    }

    /**
     * 隐藏骨架图
     */
    private void hideSkeletonScreen() {
        if (null != skeletonScreen) {
            skeletonScreen.hide();
            skeletonScreen = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (-1 != serialNo) {
            ChatBean chatBean = chatAdapter.getItem(chatAdapter.getItemCount() - 1).chatBean;
            AdvisoryBean advisoryBean = new AdvisoryBean();

            advisoryBean.id = serialNo;
            advisoryBean.type = chatBean.type;

            //咨询方发送的消息
            if (0 == chatBean.type) {
                advisoryBean.sendDate = chatBean.sendDate;
                advisoryBean.sendSontent = chatBean.content;
                advisoryBean.senderName = chatBean.senderName;
            } else {
                advisoryBean.replyDate = chatBean.sendDate;
                advisoryBean.replyContent = chatBean.content;
                advisoryBean.replyName = chatBean.senderName;
            }
            EventBus.getDefault().post(new EventMessage(Messages.UPDATE_WYZX, advisoryBean));
        }
    }
}
