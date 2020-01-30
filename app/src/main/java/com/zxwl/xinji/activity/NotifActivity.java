package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.commonlibrary.utils.NotificationHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.MessageBean;
import com.zxwl.network.bean.response.NotifBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.NotifAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 通知
 */
public class NotifActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private RecyclerView rvList;

    private NotifAdapter messageAdapter;
    private List<MessageBean> listBeans;
    private int pageNum;
    private int PAGE_SIZE = 10;

    private LoginBean.AccountBean accountBean;

    private SmartRefreshLayout refreshLayout;
    private View emptyView;
    private View errorView;

    private LinearLayout llOperation;
    private TextView tvAllSelect;
    private TextView tvDelete;

    private boolean isEdit = false;//是否处于编辑状态
    private boolean allSelect = false;//是否全选

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, NotifActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        llOperation = (LinearLayout) findViewById(R.id.ll_operation);
        tvAllSelect = (TextView) findViewById(R.id.tv_all_select);
        tvDelete = (TextView) findViewById(R.id.tv_delete);

        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        refreshLayout = findViewById(R.id.refreshLayout);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);

        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("通知");
        tvRightOperate.setText("编辑");
        tvRightOperate.setVisibility(View.VISIBLE);

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        accountBean = PreferenceUtil.getUserInfo(this);

        initRecycler();

        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getData(pageNum + 1);
            }
        });

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.autoRefresh();

        if (!EventBus.getDefault().isRegistered(this)) {
            //取消EventBus注册
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new NotificationHelper(this).onDestroy();
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);

        tvRightOperate.setOnClickListener(this);
        tvAllSelect.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notif;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                backPressed();
                break;

            case R.id.tv_right_operate:
                if (messageAdapter.getData().size() <= 0) {
                    return;
                }

                isEdit = !isEdit;

                messageAdapter.setEdit(isEdit);

                setOperationShow();
                break;

            case R.id.tv_all_select:
                isAllSelect();

                setSelectStatus(!allSelect);
                break;

            case R.id.tv_delete:
                List<Integer> allSelect = isAllSelect();
                StringBuilder deleteIndexs = new StringBuilder();
                StringBuilder deleteIds = new StringBuilder();

                for (int i = 0; i < allSelect.size(); i++) {
                    if (i == allSelect.size() - 1) {
                        deleteIndexs.append(allSelect.get(i));
                        deleteIds.append(messageAdapter.getItem(allSelect.get(i)).id);
                    } else {
                        deleteIds.append(messageAdapter.getItem(allSelect.get(i)).id + ",");
                        deleteIndexs.append(allSelect.get(i) + ",");
                    }
                }

                if (deleteIds.length() <= 0) {
                    return;
                }

                delCollectiontRequest(deleteIndexs.toString(), deleteIds.toString());
                break;
        }
    }

    private void setOperationShow() {
        if (isEdit) {
            llOperation.setVisibility(View.VISIBLE);
            tvRightOperate.setText("取消");
        } else {
            llOperation.setVisibility(View.GONE);
            tvRightOperate.setText("编辑");
        }
    }

    private void initRecycler() {
        listBeans = new ArrayList<>();
        messageAdapter = new NotifAdapter(R.layout.item_notif, listBeans);
        messageAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MessageBean messageBean = (MessageBean) adapter.getItem(position);
                if (isEdit) {
                    messageBean.isSelect = !messageBean.isSelect;
                    //判断是否有全选
                    List<Integer> allSelectList = isAllSelect();
                    if (allSelectList.size() > 0) {
                        tvDelete.setText(String.format(getResources().getString(R.string.delete_number), allSelectList.size()));
                        tvDelete.setEnabled(true);
                    } else {
                        tvDelete.setText("删除");
                        tvDelete.setEnabled(false);
                    }

                    if (allSelect) {
                        tvAllSelect.setText("全不选");
                    } else {
                        tvAllSelect.setText("全选");
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    queryNotifById(messageBean);
                }
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(messageAdapter);
    }


    /**
     * 获取收藏列表
     *
     * @param pageNum
     */
    private void getData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNotices(pageNum, PAGE_SIZE, Integer.valueOf(accountBean.id))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<MessageBean>>() {
                    @Override
                    public void onSuccess(BaseData<MessageBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<MessageBean> dataList = baseData.dataList;
                            //有数据
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                    initListBeans(dataList, true);
                                } else {
                                    setPageNum(pageNum);
                                    initListBeans(dataList, false);
                                }
                            } else {
                                if (1 == pageNum) {
                                    messageAdapter.replaceData(new ArrayList<>());
                                    messageAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                            }
                            finishRefresh(pageNum, true);
                        } else {
                            finishRefresh(pageNum, false);
                            messageAdapter.setEmptyView(errorView);
                            if (baseData.message.contains("未登录")) {
                                LoginActivity.startActivity(NotifActivity.this);
                                return;
                            }
                            Toast.makeText(NotifActivity.this.getApplicationContext(), baseData.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        messageAdapter.setEmptyView(errorView);
                        finishRefresh(pageNum, false);
                        Toast.makeText(NotifActivity.this.getApplicationContext(), "请求失败,error：" + responeThrowable.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 关闭动画
     *
     * @param pageNum
     */
    private void finishRefresh(int pageNum, boolean success) {
        if (1 == pageNum) {
            refreshLayout.finishRefresh(success);
        } else {
            refreshLayout.finishLoadMore(success);
        }
    }

    private void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    private void initListBeans(List<MessageBean> dataList, boolean isRefresh) {
        //如果是刷新则清空之前的数据
        if (isRefresh) {
            refreshLayout.resetNoMoreData();
            listBeans.clear();
        }
        //有数据就启用上拉加载
        refreshLayout.setEnableLoadMore(true);

        listBeans.addAll(dataList);
        messageAdapter.notifyDataSetChanged();

        //如果是加载更多
        if (!isRefresh && isEdit) {
            allSelect = false;
            tvAllSelect.setText("全选");
        }
    }

    /**
     * 删除数据
     *
     * @param deleteIndex
     */
    private void deleteRequest(String deleteIndex) {
        Iterator<MessageBean> it = listBeans.iterator();
        while (it.hasNext()) {
            MessageBean collectionBean = it.next();
            if (collectionBean.isSelect) {
                it.remove();
            }
        }
        tvDelete.setEnabled(false);
        tvDelete.setText("删除");
        if (messageAdapter.getData().size() > 0) {
            messageAdapter.notifyDataSetChanged();
        } else {
            messageAdapter.setEmptyView(emptyView);
            isEdit = false;
            messageAdapter.setEdit(isEdit);
            setOperationShow();
            messageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param deleteIndexs 条目的下标
     * @param deleteIds    条目的收藏id
     */
    private void delCollectiontRequest(String deleteIndexs, String deleteIds) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .delNotice(deleteIds)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            deleteRequest(deleteIndexs);
                        } else {
                            Toast.makeText(NotifActivity.this.getApplicationContext(), "删除通知失败," + baseData.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Toast.makeText(NotifActivity.this.getApplicationContext(), "删除通知失败", Toast.LENGTH_SHORT).show();
                    }
                });
//        HandlerThread
    }

    /**
     * 查询消息
     */
    private void queryNotifById(MessageBean messageBean) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNoticeById(String.valueOf(messageBean.id))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<NotifBean>() {
                    @Override
                    public void onSuccess(NotifBean baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            messageBean.state = 1;
                            messageAdapter.notifyDataSetChanged();
                        } else {
//                            ToastHelper.showShort("baseData");
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
//                        ToastHelper.showShort("onerr");
                    }
                });
    }


    /**
     * 获取全选的列表
     */
    private List<Integer> isAllSelect() {
        //选择的数量
        List<Integer> selectIndexs = new ArrayList<>();
        for (int i = 0; i < listBeans.size(); i++) {
            if (listBeans.get(i).isSelect) {
                selectIndexs.add(i);
            }
        }

        allSelect = (selectIndexs.size() == listBeans.size());
        return selectIndexs;
    }

    /**
     * 设置全部的状态
     */
    private void setSelectStatus(boolean select) {
        for (int i = 0; i < listBeans.size(); i++) {
            listBeans.get(i).isSelect = select;
        }
        messageAdapter.notifyDataSetChanged();

        if (select) {
            tvAllSelect.setText("全不选");
            tvDelete.setText(String.format(getResources().getString(R.string.delete_number), listBeans.size()));
            tvDelete.setEnabled(true);
        } else {
            tvAllSelect.setText("全选");
            tvDelete.setText("删除");
            tvDelete.setEnabled(false);
        }
    }

//    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
//    //之后EventBus会自动扫描到此函数，进行数据传递
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getData(EventMessage eventMessage) {
//        List<MessageBean> messageBeanList = null;
//        switch (eventMessage.message) {
//            //删除对应的消息
//            case Messages.DEL_NOTIF:
//                Integer msgId = (Integer) eventMessage.t;
//                messageBeanList = adapter.getData();
//                int position = -1;
//                for (int i = 0; i < messageBeanList.size(); i++) {
//                    MessageBean messageBean = messageBeanList.get(i);
//                    if (msgId == messageBean.id) {
//                        position = i;
//                    }
//                }
//                if (position != -1) {
//                    adapter.remove(position);
//                }
//                break;
//
//            //删除所有组织邀请消息
//            case Messages.DEL_ALL_NOTIF:
//                messageBeanList = adapter.getData();
//
//                Iterator<MessageBean> iterator = messageBeanList.iterator();
//                while (iterator.hasNext()) {
//                    MessageBean next = iterator.next();
//                    if (next.type == 2) {
//                        iterator.remove();
//                    }
//                }
//                adapter.setEmptyView(emptyView);
//                adapter.notifyDataSetChanged();
//                break;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister();

        int unReadNumber = 0;

        for (MessageBean messageBean : messageAdapter.getData()) {
            if (messageBean.state == 0) {
                unReadNumber++;
            }
        }

        if (unReadNumber > 0) {
            EventBus.getDefault().post(new EventMessage(Messages.NEW_NOTIF, String.valueOf(unReadNumber)));
        }
    }

    private void unRegister() {
        if (EventBus.getDefault().isRegistered(this)) {
            //取消EventBus注册
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backPressed();
    }

    private void backPressed() {
//        if (1 != ServiceHelper.isAppAlive(this, getPackageName()) || !ServiceHelper.isExsitMianActivity(this, MainActivity.class)) {
//            ServiceHelper.startActivityWithAppIsRuning(this, getPackageName());
//        } else if (ServiceHelper.isExsitMianActivity(this, MainActivity.class)) {
//            finish();
//        }
        finish();
    }
}
