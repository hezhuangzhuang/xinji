package com.zxwl.xinji.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.activity.LoadingActivity;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.VideoCallAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import qdx.stickyheaderdecoration.NormalDecoration;

/**
 * 视频呼叫
 */
public class VideoCallActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;

    private RecyclerView rvList;

    /**
     * 视频呼叫
     ****/
    private RelativeLayout rlAddress;
    private ImageView ivBack;
    private TextView tvAddress;

    /**
     * 视频会议
     ********/
    private RelativeLayout rlSelect;
    private TextView tvAllSelect;
    private TextView tvSelectNumber;
    private TextView tvCall;

    /**
     * 已选择
     ********/
    private TextView tvLeftOperate;
    private TextView tvRightOperate;

    private VideoCallAdapter addressAdapter;
    private NormalDecoration decoration;

    private LoginBean.AccountBean accountBean;

    public static final String SELECT_SITE = "SELECT_SITE";
    public static final String TITLE = "TITLE";
    private String title;

    public static final String TYPE_SPHJ = "视频呼叫";

    public static final String TYPE_SPHY = "视频会议";

    public static final String TYPE_YXZ = "已选择";

    //当前请求的单位id
    private int lastUnitId;
    private String lastUnitName;

    private int adapterMode;

    //是否徐安全
    private boolean isAllSelect = false;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, VideoCallActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    public static void startActivity(Activity context, String title, Set<DepartmentBean> selectList) {
        Intent intent = new Intent(context, VideoCallActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(SELECT_SITE, (Serializable) selectList);

        context.startActivityForResult(intent, VideoCallAdapter.MODE_YXZ);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        tvLeftOperate = (TextView) findViewById(R.id.tv_left_operate);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);

        rlAddress = (RelativeLayout) findViewById(R.id.rl_address);
        tvAllSelect = (TextView) findViewById(R.id.tv_all_select);
        rlSelect = (RelativeLayout) findViewById(R.id.ll_select);
        tvSelectNumber = (TextView) findViewById(R.id.tv_select_number);
        tvCall = (TextView) findViewById(R.id.tv_call);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);
        tvTopTitle.setText(title);

        accountBean = PreferenceUtil.getUserInfo(this);

        switch (title) {
            //视频呼叫
            case TYPE_SPHJ:
                tvAddress.setText(accountBean.unitName);

                adapterMode = VideoCallAdapter.MODE_SPHJ;

                initAdapter(true, new ArrayList<DepartmentBean>());

                getDepartmentList(accountBean.unitId, accountBean.unitName, null);
                break;

            //视频会议
            case TYPE_SPHY:
                tvAddress.setText(accountBean.unitName);
                tvAllSelect.setVisibility(View.VISIBLE);
                rlSelect.setVisibility(View.VISIBLE);

                adapterMode = VideoCallAdapter.MODE_SPHY;

                initAdapter(true, new ArrayList<DepartmentBean>());

                getDepartmentList(accountBean.unitId, accountBean.unitName, null);
                break;

            //已选择
            case TYPE_YXZ:
                rlAddress.setVisibility(View.GONE);
                rlSelect.setVisibility(View.GONE);

                ivBackOperate.setVisibility(View.GONE);

                tvLeftOperate.setText("取消");
                tvRightOperate.setText("确定");

                tvLeftOperate.setVisibility(View.VISIBLE);
                tvRightOperate.setVisibility(View.VISIBLE);

                adapterMode = VideoCallAdapter.MODE_YXZ;

                selectList = (Set<DepartmentBean>) getIntent().getSerializableExtra(SELECT_SITE);

                initAdapter(false, new ArrayList<>(selectList));
                break;
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        tvLeftOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);

        tvAllSelect.setOnClickListener(this);

        tvCall.setOnClickListener(this);

        tvSelectNumber.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_call;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
            case R.id.tv_left_operate:
                finish();
                break;

            case R.id.iv_back:
                if (null != currentDepartmentBean) {
                    getDepartmentList(currentDepartmentBean.parentId, lastUnitName, currentDepartmentBean);
                } else {
                    getDepartmentList(accountBean.unitId, accountBean.unitName, null);
                }
                break;

            //全选
            case R.id.tv_all_select:
                isAllSelect = !isAllSelect;

                for (int i = 0; i < addressAdapter.getItemCount(); i++) {
                    addressAdapter.getItem(i).isCheck = isAllSelect;
                }

                if (isAllSelect) {
                    selectList.addAll(addressAdapter.getData());
                } else {
                    selectList.removeAll(addressAdapter.getData());
                }

                addressAdapter.notifyDataSetChanged();

                tvAllSelect.setText(isAllSelect ? "全不选" : "全选");

                setSelectNumber(selectList.size());
                break;

            //呼叫
            case R.id.tv_call:
                if (selectList.size() <= 0) {
                    ToastHelper.showShort("请选择参会列表");
                    return;
                }
                StringBuilder builder = new StringBuilder();
                for (DepartmentBean departmentBean : selectList) {
                    builder.append(departmentBean.siteId + ",");
                }
                createConfRequest(builder.toString());
                break;

            //已选的乡镇数量
            case R.id.tv_select_number:
                if (selectList.size() <= 0) {
                    ToastHelper.showShort("请选择乡镇后再查看");
                    break;
                }
                VideoCallActivity.startActivity(VideoCallActivity.this, TYPE_YXZ, selectList);
                break;

            case R.id.tv_right_operate:
                Intent intent = new Intent();
                intent.putExtra(SELECT_SITE, new HashSet<>(addressAdapter.getData()));
                // 设置返回码和返回携带的数据
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
    }

    //当前选择的bean
    private DepartmentBean currentDepartmentBean;

    private Set<DepartmentBean> selectList = new HashSet<>();

    /**
     * 是否显示头部悬浮框
     *
     * @param showDecoration  true:显示，false：不显示
     * @param departmentBeans
     */
    private void initAdapter(boolean showDecoration, ArrayList<DepartmentBean> departmentBeans) {
        addressAdapter = new VideoCallAdapter(R.layout.item_call, departmentBeans);
        addressAdapter.setMode(adapterMode);
        addressAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                DepartmentBean departmentBean = null;
                switch (title) {
                    //视频呼叫
                    case TYPE_SPHJ:
                        if (view instanceof ConstraintLayout) {
                            departmentBean = addressAdapter.getItem(position);
                            getDepartmentList(departmentBean.id, departmentBean.departmentName, departmentBean);
                        } //点击呼叫按钮
                        else {
                            departmentBean = addressAdapter.getItem(position);

                            createConfRequest(departmentBean.siteId+",");
//                            HuaweiCallImp.getInstance().callSite(departmentBean.terUri);
                        }
                        break;

                    case TYPE_SPHY:
                        if (view instanceof ConstraintLayout) {
                            departmentBean = addressAdapter.getItem(position);
                            getDepartmentList(departmentBean.id, departmentBean.departmentName, departmentBean);
                        }//点击选择框
                        else {
                            departmentBean = addressAdapter.getItem(position);
                            //是否选择
                            departmentBean.isCheck = !departmentBean.isCheck;

                            if (departmentBean.isCheck) {
                                selectList.add(departmentBean);
                            } else {
                                selectList.remove(departmentBean);
                            }

                            //设置全选按钮的状态
                            setAllSelectStatus();

                            //设置选择的乡镇数量
                            setSelectNumber(selectList.size());

                            addressAdapter.notifyDataSetChanged();
                        }
                        break;

                    case TYPE_YXZ:
                        addressAdapter.getData().remove(position);
                        addressAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        if (showDecoration) {
            decoration = new NormalDecoration() {
                @Override
                public String getHeaderName(int pos) {
                    return addressAdapter.getItem(pos).firstChar;
                }
            };
            rvList.addItemDecoration(decoration);
        }
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(addressAdapter);
    }

    public void setAllSelectStatus() {
        //当前列表是否全选
        boolean tempAllSelect = true;

        for (int i = 0; i < addressAdapter.getItemCount(); i++) {
            //如果有一个不是选择状态则非全选
            if (!addressAdapter.getItem(i).isCheck) {
                tempAllSelect = false;
            }
        }

        //设置全选按钮
        isAllSelect = tempAllSelect;
        tvAllSelect.setText(isAllSelect ? "全不选" : "全选");
    }

    /**
     * 设置已选的数量
     *
     * @param selectNumber
     */
    public void setSelectNumber(int selectNumber) {
        tvSelectNumber.setText("已选择：" + selectNumber + "个单位");
    }

    /**
     * 添加item
     *
     * @param newList
     */
    private void addItemDecoration(List<DepartmentBean> newList) {
        addressAdapter.replaceData(newList);
        decoration.onDestory();
        rvList.removeItemDecorationAt(0);
        decoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                return addressAdapter.getItem(pos).firstChar;
            }
        };
        rvList.addItemDecoration(decoration);
    }

    /**
     * 获取组织信息
     */
    private void getDepartmentList(int currentUnitId, String unitName, DepartmentBean departmentBean) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartment(currentUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DepartmentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DepartmentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DepartmentBean> tempList = baseData.dataList;
                            List<DepartmentBean> dataList = new ArrayList<>();


                            for (int i = 0; i < tempList.size(); i++) {
                                if (!TextUtils.isEmpty(tempList.get(i).siteId) && !TextUtils.isEmpty(tempList.get(i).terUri)) {
                                    dataList.add(tempList.get(i));
                                }
                            }

                            if (null != dataList && dataList.size() > 0) {
                                currentDepartmentBean = departmentBean;

                                //视频会议
                                if (TYPE_SPHY.equals(title)) {
                                    //判断是否在已选列表里
                                    for (int i = 0; i < dataList.size(); i++) {
                                        if (selectList.contains(dataList.get(i))) {
                                            dataList.get(i).isCheck = true;
                                        }
                                    }

                                    //添加悬浮的头部
                                    addItemDecoration(dataList);

                                    //设置全选按钮的状态
                                    setAllSelectStatus();
                                } //视频呼叫
                                else if (TYPE_SPHJ.equals(title)) {
                                    //添加悬浮的头部
                                    addItemDecoration(dataList);
                                }

                                //获取上一次的bean
                                lastUnitName = tvAddress.getText().toString();

                                //设置现在的名称
                                tvAddress.setText(unitName);

                                if(1 != accountBean.level){
                                    ivBack.setVisibility(View.GONE);
                                }else {
                                    if (1 != currentUnitId ) {
                                        ivBack.setVisibility(View.VISIBLE);
                                    } else {
                                        ivBack.setVisibility(View.GONE);
                                    }
                                }
                            }
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case VideoCallAdapter.MODE_YXZ:
                    selectList = (Set<DepartmentBean>) data.getSerializableExtra(SELECT_SITE);
                    for (int i = 0; i < addressAdapter.getItemCount(); i++) {
                        if (selectList.contains(addressAdapter.getItem(i))) {
                            addressAdapter.getItem(i).isCheck = true;
                        } else {
                            addressAdapter.getItem(i).isCheck = false;
                        }
                    }
                    //设置全选的状态
                    setAllSelectStatus();
                    //设置已选按钮
                    setSelectNumber(selectList.size());

                    addressAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 创建会议
     */
    private void createConfRequest(String unitIdsAndSiteIds) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
//                .setBaseUrl(!TextUtils.isEmpty(Urls.CREATE_BASE_URL)?Urls.BASE_URL:"")
                .setBaseUrl(Urls.CREATE_BASE_URL)
                .builder(StudyApi.class)
                .createConf(unitIdsAndSiteIds, accountBean.id)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));

                            LoadingActivity.startActivty(VideoCallActivity.this, "");

                            //是否自己创建的会议
                            PreferencesHelper.saveData(UIConstants.IS_CREATE, true);

                            //是否需要自动接听
                            PreferencesHelper.saveData(UIConstants.IS_AUTO_ANSWER, true);

                            ToastHelper.showShort(baseData.message);

                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("error" + responeThrowable.message);
                    }
                });
    }
}
