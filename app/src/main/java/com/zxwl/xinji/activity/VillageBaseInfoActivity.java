package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.DepartmentDetailsBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.FullyGridLayoutManager;
import com.zxwl.xinji.adapter.ShowImageAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 村子基本信息
 */
public class VillageBaseInfoActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private RelativeLayout rlScreen;
    private TextView tvCity;
    private TextView tvScreen;
    private TextView tvInfo;
    private RecyclerView rvList;

    public static final String TYPE_JBXX = "基本信息";

    public static final String TYPE_DJZD = "党建阵地";

    public static final String IS_BASE_INFO = "IS_BASE_INFO";
    public static final String UNIT_ID = "UNIT_ID";

    /**
     * true：基本信息，false：党建阵地
     */
    private boolean isBasicInfo = false;

    //村子id
    private int unitId;

    private ShowImageAdapter selectImageAdapter;
    private List<LocalMedia> selectListImage;

    private DepartmentDetailsBean.DataBean currentDetailsBean;

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context, boolean isBasic, int unitId) {
        Intent intent = new Intent(context, VillageBaseInfoActivity.class);
        intent.putExtra(UNIT_ID, unitId);
        intent.putExtra(IS_BASE_INFO, isBasic);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        rlScreen = (RelativeLayout) findViewById(R.id.rl_screen);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvScreen = (TextView) findViewById(R.id.tv_screen);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        accountBean = PreferenceUtil.getUserInfo(this);

        isBasicInfo = getIntent().getBooleanExtra(IS_BASE_INFO, false);
        unitId = getIntent().getIntExtra(UNIT_ID, 1);

        tvTopTitle.setText(isBasicInfo ? "基本信息" : "党建阵地");
        tvScreen.setText("切换地区");

        tvRightOperate.setText("编辑");
        tvRightOperate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.ic_add_consultation, 0);

        //普通账号不显示
        tvRightOperate.setVisibility(accountBean.checkAdmin == 1 ? View.VISIBLE : View.GONE);

        //普通账号不显示
        rlScreen.setVisibility(accountBean.checkAdmin == 1 ? View.VISIBLE : View.GONE);

        tvCity.setText(accountBean.unitName);

        initAdapter();

        //查询单位详情
        queryUnitDetail();

        register();
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvScreen.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_village_base_info;
    }

    /**
     * 初始化reycler
     */
    private void initAdapter() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(manager);
        selectImageAdapter = new ShowImageAdapter(R.layout.gv_filter_image, new ArrayList<>());

        selectImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectImageAdapter.getData().size() > 0) {
                    LocalMedia media = (LocalMedia) selectImageAdapter.getItem(position);

                    int mediaType = PictureMimeType.pictureToVideo(media.getPictureType());

                    //判断类型
                    switch (mediaType) {
                        case PictureConfig.TYPE_IMAGE:
                            // 预览图片 可自定长按保存路径
                            PictureSelector.create(VillageBaseInfoActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectListImage);
                            break;

                        case PictureConfig.TYPE_VIDEO:
                            // 预览视频
                            PictureSelector.create(VillageBaseInfoActivity.this).externalPictureVideo(media.getPath());
                            break;

                        case 3:
                            // 预览音频
                            PictureSelector.create(VillageBaseInfoActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
        rvList.setAdapter(selectImageAdapter);
    }

    /**
     * 查询详情
     */
    private void queryUnitDetail() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartmentDetail("departmentInfoAction_queryDetail.action",unitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<DepartmentDetailsBean>() {
                    @Override
                    public void onSuccess(DepartmentDetailsBean detailsBean) {
                        if (BaseData.SUCCESS.equals(detailsBean.result)) {
                            DepartmentDetailsBean.DataBean newData = detailsBean.data;
                            if (null != newData) {
                                currentDetailsBean = newData;
                                setUnitInfo(newData);
                            }
                        } else {
                            ToastHelper.showShort(detailsBean.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 设置单位信息
     *
     * @param newData
     */
    private void setUnitInfo(DepartmentDetailsBean.DataBean newData) {
        if (isBasicInfo) {
            tvInfo.setText(newData.baseInfo);
            getImageList(Arrays.asList(
                    newData.pic1,
                    newData.pic2,
                    newData.pic3,
                    newData.pic4,
                    newData.pic5,
                    newData.pic6,
                    newData.pic7,
                    newData.pic8,
                    newData.pic9
            ));
        } else {
            tvInfo.setText(newData.unitInfo);
            getImageList(Arrays.asList(
                    newData.pic11,
                    newData.pic22,
                    newData.pic33,
                    newData.pic44,
                    newData.pic55,
                    newData.pic66,
                    newData.pic77,
                    newData.pic88,
                    newData.pic99
            ));
        }
        selectImageAdapter.replaceData(selectListImage);
    }

    /**
     * 获取图片列表
     */
    private void getImageList(List<String> imageUrls) {
        if (null == selectListImage) {
            selectListImage = new ArrayList<>();
        }
        selectListImage.clear();
        LocalMedia localMedia = null;
        for (String url : imageUrls) {
            if (!TextUtils.isEmpty(url)) {
                localMedia = new LocalMedia();
                localMedia.setPath(url);
                selectListImage.add(localMedia);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                EditUnitInfoActivity.startActivity(VillageBaseInfoActivity.this, currentDetailsBean, isBasicInfo);
                break;

            case R.id.tv_screen:
                //对话框已经显示
                if (null != screenCityDialog && screenCityDialog.isShowing()) {
                    screenCityDialog.dismiss();
                } else {
                    if (null != screenCityDialog) {
                        showCityDialog();
                    } else {
                        getDepartmentList(1);
                    }
                }
                break;
        }
    }

    /*********************************************地址的筛选框---start******************************/
    //乡镇ID
    private int townshipId;
    //街村ID
    private int villageId;

    //乡镇名称
    private String townshipName;
    //街村名称
    private String villageName;

    //呼叫的号码
    private String callNumber;

    //左边全辛集市的id
    private int LEFT_ALL_ID = 0x1111;

    //右边全部的id
    private int RIGHT_ALL_ID = 0x1112;

    private List<DepartmentBean> leftDepartments;

    /**
     * 获取组织信息
     */
    private void getDepartmentList(int currentUnitId) {
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
                            List<DepartmentBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                DepartmentBean departmentBean = null;

                                //如果左边的列表等于null，代表第一次请求
                                if (null == leftDepartments) {
                                    leftDepartments = dataList;

                                    //添加辛集市在左边
                                    departmentBean = new DepartmentBean();
                                    departmentBean.id = 1;
                                    departmentBean.departmentName = "辛集市";
                                    leftDepartments.add(0, departmentBean);

                                    //如果是用户bean为空
                                    showScreenDialog(leftDepartments, new ArrayList<>());
                                } else {
                                    //代表点击的辛集市
                                    if (1 == townshipId) {
                                        //添加辛集市在左边
                                        departmentBean = new DepartmentBean();
                                        departmentBean.id = 1;
                                        departmentBean.departmentName = "辛集市";
                                        dataList.add(0, departmentBean);
                                    }
                                    screenCityDialog.setRightNewData(dataList);
                                }
                            } else {
                                if (null != leftDepartments) {
                                    screenCityDialog.setRightNewData(new ArrayList<>());
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

    private ScreenCityPopupWindow screenCityDialog;

    /**
     * 显示筛选对话框
     */
    private void showScreenDialog(List<DepartmentBean> leftData,
                                  List<DepartmentBean> rightData) {
        if (null == screenCityDialog) {
            screenCityDialog = new ScreenCityPopupWindow(
                    this,
                    DisplayUtil.getScreenWidth(),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    leftData,
                    rightData,
                    true
            );

            screenCityDialog.setOnScreenClick(new ScreenCityPopupWindow.onScreenClick() {
                @Override
                public void onLeftClick(int cityId, String departmentName) {
                    townshipName = departmentName;
                    if (LEFT_ALL_ID == cityId) {
                        if (2 == accountBean.level) {
                            townshipId = accountBean.unitId;
                        } else {
                            townshipId = 0;
                        }
                        villageId = 0;

                        villageName = "";

                        screenCityDialog.setRightNewData(new ArrayList<>());
                        screenCityDialog.dismiss();
                    } else {
                        townshipId = cityId;
                        getDepartmentList(cityId);
                    }
                }

                @Override
                public void onRightClick(int cityId, String departmentName, String terUri) {
                    if (RIGHT_ALL_ID == cityId) {
                        villageId = 0;
                        villageName = "";
                        screenCityDialog.dismiss();
                    } else {
                        //如果没登录，并且是微心愿
                        villageId = cityId;

                        villageName = departmentName;

                        screenCityDialog.dismiss();

                        //置空左边的数据
//                        leftDepartments = null;

                        tvRightOperate.setVisibility(villageId != Integer.valueOf(accountBean.unitId) ? View.GONE : View.VISIBLE);

                        unitId = villageId;
                        tvCity.setText(villageName);
                        callNumber = terUri;

                        //查询村庄详情
                        queryUnitDetail();
                    }
                }
            });
        }
        showCityDialog();
    }

    private void showCityDialog() {
        screenCityDialog.setAlignBackground(false);
        screenCityDialog.showPopupWindow(rlScreen);
//        screenCityDialog.setPopupGravity(Gravity.BOTTOM);
    }

    /*********************************************地址的筛选框---end******************************/

    @Override
    public void onDestroy() {
        super.onDestroy();

        unRegister();
    }

    private void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBsus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    private void unRegister() {
        if (EventBus.getDefault().isRegistered(this)) {
            //注册EventBus,注意参数是this，传入activity会报错
            EventBus.getDefault().unregister(this);
        }
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRecycler(EventMessage eventMessage) {
        switch (eventMessage.message) {
            case Messages.UPDATE_UNIT:
                DepartmentDetailsBean.DataBean newData = (DepartmentDetailsBean.DataBean) eventMessage.t;

                currentDetailsBean = newData;

                setUnitInfo(newData);
                break;
        }
    }
}
