package com.zxwl.xinji.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.orhanobut.logger.Logger;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MyLocationOverlay;
import com.tianditu.android.maps.TMapLayerManager;
import com.tianditu.android.maps.overlay.PolylineOverlay;
import com.tianditu.android.maps.renderoption.PlaneOption;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.LogUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.LonlatBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.interceptor.CommonLogger;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.MineActivity;
import com.zxwl.xinji.activity.OrganizingLifeDetailsActivity;
import com.zxwl.xinji.activity.SearchActivity;
import com.zxwl.xinji.adapter.AddressAdapter;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.indexbar.IndexBar;
import com.zxwl.xinji.widget.indexbar.IndexLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import qdx.stickyheaderdecoration.NormalDecoration;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 组织生活
 */
public class OrganizingLifeFragment extends BaseLazyFragment implements View.OnClickListener, LocBroadcastReceiver {
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private TextView tvZtdr;
    private TextView tvShyk;
    private TextView tvMzpy;
    private TextView tvZzshh;
    private TextView tvMore;
    private TextView tvInfo;

    private RecyclerView rvList;
    private AddressAdapter addressAdapter;
    private LinearLayoutManager linearLayoutManager;
    private IndexLayout indexLayout;

    private FrameLayout flMap;
    private MapView mapView;

    private FrameLayout flAddress;
    private TextView tvAddress;

    private ImageView ivSwitch;

    //true显示地图，false显示列表
    private boolean showMap = true;

    private TMapLayerManager mLayerMnger;
    private MyOverlay myLocationOverlay;

    private LoginBean.AccountBean accountBean;

    private View errorView;

    private DepartmentBean currentDepartmentBean;//当前列表的父节点

    //当前单位父id
    private int mParentUnitId = -1;
    //当前单位的id
    private int mUnitId = -1;

    //村庄名
    private String mDescriptionName;

    //单位名
    private int mUnitName;

    private List<LonlatBean> allLonlatBeans;

    private WeakReference<Activity> weakReference;

    private ImageView ivBack;

    private String region;

    public OrganizingLifeFragment() {
        // Required empty public constructor
    }

    public static OrganizingLifeFragment newInstance() {
        OrganizingLifeFragment fragment = new OrganizingLifeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_organizing_life, container, false);
    }

    @Override
    protected void findViews(View view) {
        Log.i(TAG, TAG + "-->findViews");

        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_title);
        tvZtdr = (TextView) view.findViewById(R.id.tv_ztdr);
        tvShyk = (TextView) view.findViewById(R.id.tv_shyk);
        tvMzpy = (TextView) view.findViewById(R.id.tv_mzpy);
        tvZzshh = (TextView) view.findViewById(R.id.tv_zzshh);
        tvMore = (TextView) view.findViewById(R.id.tv_more);

        tvInfo = (TextView) view.findViewById(R.id.tv_info);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);
        indexLayout = (IndexLayout) view.findViewById(R.id.index_layout);

        flMap = (FrameLayout) view.findViewById(R.id.fl_map);
        flAddress = (FrameLayout) view.findViewById(R.id.fl_address);
        tvAddress = (TextView) view.findViewById(R.id.tv_address);

        ivSwitch = (ImageView) view.findViewById(R.id.iv_switch);

        mapView = (MapView) view.findViewById(R.id.mapview);

        ivBack = (ImageView) view.findViewById(R.id.iv_back);

        tvRightOperate = (TextView) view.findViewById(R.id.tv_right_operate);

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryDepartment(mUnitId, mParentUnitId, mDescriptionName, currentDepartmentBean);
            }
        });
    }

    @Override
    protected void addListeners() {
        tvZtdr.setOnClickListener(this);
        tvShyk.setOnClickListener(this);
        tvMzpy.setOnClickListener(this);
        tvZzshh.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvInfo.setOnClickListener(this);

        ivSwitch.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        tvRightOperate.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("组织生活");

        //如果用户信息不为空则请求数据
        if (isLogin() && null != PreferenceUtil.getUserInfo(getActivity())) {
            Log.i(TAG, TAG + "-->initData");

            weakReference = new WeakReference<Activity>(getActivity());

            allLonlatBeans = new ArrayList<>();

            initRecycler();

            accountBean = PreferenceUtil.getUserInfo(getActivity());

            //登录华为平台
            HuaweiLoginImp.getInstance().loginRequest(getActivity(), accountBean.siteAccount, accountBean.sitePwd, Urls.SMC_REGISTER_SERVER, Urls.SMC_REGISTER_PORT, "", "", "", "");

            //初始化map
            initMap(new ArrayList<>());

            region = getString(R.string.region);

            String[] strings = region.split(",");

            drawLine(strings);

            tvAddress.setText(accountBean.unitName);

            //查询经纬度
            queryLonlats(accountBean.unitId, accountBean.unitId, accountBean.unitName, null, true);

            //查询组织单位
            queryDepartment(accountBean.unitId, accountBean.unitId, accountBean.unitName, null);

            LocBroadcast.getInstance().registerBroadcast(this, mActions);
        }
    }

    /**
     * 绘制市的范围
     *
     * @param strings
     */
    private void drawLine(String[] strings) {
        ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            String[] lon = string.split(" ");

            points.add(new GeoPoint((int) (Double.valueOf(lon[1]) * 1E6), (int) (Double.valueOf(lon[0]) * 1E6)));
        }

        PlaneOption option = new PlaneOption();
        option.setStrokeWidth(5);
        option.setDottedLine(false);
        option.setFillColor(ContextCompat.getColor(getActivity(), R.color.color_facd8f));
        option.setStrokeColor(ContextCompat.getColor(getActivity(), R.color.color_facd8f));

        PolylineOverlay overlay = new PolylineOverlay();
        overlay.setOption(option);
        overlay.setPoints(points);
        mapView.addOverlay(overlay);
    }

    private void initMap(List<LonlatBean> lonlatBeans) {
//        mapView.setLogoPos(MapView.LOGO_NONE);//不显示logo
//        mapView.setBuiltInZoomControls(false);//不显示缩放按钮
//        mapView.enableRotate(false);//不能旋转
//        mapView.setMapType(MapView.TMapType.MAP_TYPE_VEC);//设置为矢量图

        //得到mMapView的控制权,可以用它控制和驱动平移和缩放
//        mapController = mapView.getController();
//        //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
//        GeoPoint point = new GeoPoint((int) (115.230391 * 1E6), (int) (38.065593 * 1E6));
//        //设置地图中心点
//        mapController.setCenter(point);

        //设置定位
//        mMyLocation = new MyOverlay(getActivity(), mapView);
//        mMyLocation.setGpsFollow(true);//设置跟随
//        mMyLocation.enableCompass();//显示指南针
//        mMyLocation.enableMyLocation();//显示我的位置
//        mapView.addOverlay(mMyLocation);

        mapView.setLogoPos(MapView.LOGO_NONE);//不显示logo

        myLocationOverlay = new MyOverlay(weakReference.get(), mapView);
        myLocationOverlay.enableMyLocation();

        // 图层管理类
        mLayerMnger = new TMapLayerManager(mapView);

        //设置只显示图
        mLayerMnger.setLayersShow(null);

        //绘制图标
//        drawOverlay(lonlatBeans, null);
    }

    /**
     * 绘制覆盖物
     */
    private void drawOverlay(List<LonlatBean> lonlatBeans, LonlatBean newLonlatBean, boolean forceRefresh) {
        //绘制每个覆盖物
        for (LonlatBean lonlatBean : lonlatBeans) {
            GeoPoint geoPoint = new GeoPoint((int) (lonlatBean.latitude * 1E6), (int) (lonlatBean.longitude * 1E6));

            //创建覆盖物
            initPopup(geoPoint, lonlatBean);
        }

        //设置中心点
        if (forceRefresh && Double.valueOf(accountBean.longitude) > 0) {
            setCenter(Double.valueOf(accountBean.latitude), Double.valueOf(accountBean.longitude));
        } else if (null != newLonlatBean) {
            setCenter(newLonlatBean.latitude, newLonlatBean.longitude);
        } else if (lonlatBeans.size() > 0) {
            setCenter(lonlatBeans.get(0).latitude, lonlatBeans.get(0).longitude);
        }

        if (null != newLonlatBean) {
            //设置缩放比例
            int zoomLevel = mapView.getZoomLevel();
            setZoom(zoomLevel + 1);
        } else {
            if (lonlatBeans.size() > 0) {
                int level = getLevel();
                setZoom(level);
            }
        }
    }

    private int getLevel() {
        if (accountBean.level == 1) {
            return 10;
        } else if (accountBean.level == 2) {
            return 11;
        } else {
            return 12;
        }
    }

    /**
     * 设置中心点
     *
     * @param lat
     * @param lon
     */
    private void setCenter(double lat, double lon) {
        GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
        mapView.getController().setCenter(geoPoint);

        double dLonSpan = 0.1;
        double dLatSpan = 0.1;

        mapView.getController().zoomToSpan((int) (dLatSpan * 1E6), (int) (dLonSpan * 1E6));
    }

    private void setZoom(int zoomLevel) {
        mapView.getController().setZoom(zoomLevel);
    }

    private TextView mPopView;

    private void initPopup(GeoPoint point, LonlatBean lonlatBean) {
        // 创建弹出框view
        mPopView = (TextView) getLayoutInflater().inflate(R.layout.map_popview, null);
        mPopView.setText(lonlatBean.departmentName);
        mPopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果级别是街村
                if (3 != lonlatBean.areaType) {
                    //查询
                    queryLonlats(lonlatBean.departmentId, lonlatBean.parentId, lonlatBean.departmentName, lonlatBean, false);
                } else {
                    OrganizingLifeDetailsActivity.startActivity(getActivity(),
                            0,
                            lonlatBean.departmentId,
                            lonlatBean.departmentName,
                            lonlatBean.departmentName,
                            lonlatBean.parentId,
                            lonlatBean.siteUri
                    );
                }
            }
        });

        mapView.addView(mPopView, new MapView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                point,
                MapView.LayoutParams.BOTTOM_CENTER)
        );
    }

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }


    private NormalDecoration decoration;

    private void initRecycler() {
        //初始化适配器
        initAddressAdapter();
    }

    private void initIndexBar(List<DepartmentBean> departmentBeans) {
        //侧边导航栏
        List<String> heads = new ArrayList<>();

        //遍历列表数据里的
        for (DepartmentBean departmentBean : departmentBeans) {
            if (!heads.contains(departmentBean.firstChar)) {
                heads.add(departmentBean.firstChar);
            }
        }

        indexLayout.setIndexBarHeightRatio(1f);
        indexLayout.getIndexBar().setIndexsList(heads);
        indexLayout.setCircleTextColor(Color.WHITE);
        indexLayout.setCircleRadius(200);
        indexLayout.setCirCleTextSize(150);
        indexLayout.setCircleColor(ContextCompat.getColor(getActivity(), R.color.color_999));
        indexLayout.getIndexBar().setIndexChangeListener(new IndexBar.IndexChangeListener() {
            @Override
            public void indexChanged(String indexName) {
                for (int i = 0; i < departmentBeans.size(); i++) {
                    if (indexName.equals(departmentBeans.get(i).firstChar)) {
                        linearLayoutManager.scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
    }

    @NonNull
    private void initAddressAdapter() {
        List<DepartmentBean> departmentBeans = new ArrayList<>();

        addressAdapter = new AddressAdapter(R.layout.item_address, departmentBeans);

        addressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DepartmentBean departmentBean = addressAdapter.getItem(position);
                //判断是否是父
                if (1 == departmentBean.isParent) {
                    //查询子列表
                    queryDepartment(departmentBean.id, departmentBean.parentId, departmentBean.departmentName, departmentBean);
                } else {
                    OrganizingLifeDetailsActivity.startActivity(getActivity(),
                            0,
                            departmentBean.id,
                            departmentBean.departmentName,
                            departmentBean.departmentName,
                            departmentBean.parentId,
                            departmentBean.terUri);
                }
            }
        });

        if (departmentBeans.size() > 0) {
            decoration = new NormalDecoration() {
                @Override
                public String getHeaderName(int pos) {
                    return departmentBeans.get(pos).firstChar;
                }
            };
            rvList.addItemDecoration(decoration);
        }
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(linearLayoutManager);
        rvList.setAdapter(addressAdapter);
    }

    private void updateRecyclerDate(List<DepartmentBean> selectData) {
        addressAdapter.replaceData(selectData);

        if (null != decoration) {
            //移除之前的decoration
            decoration.onDestory();
            rvList.removeItemDecorationAt(0);
        }

        decoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                return selectData.get(pos).firstChar;
            }
        };

        rvList.addItemDecoration(decoration);

        rvList.smoothScrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_content:
                SearchActivity.startActivity(getActivity());
                break;

            //返回上一级
            case R.id.iv_back:
                if (null != currentDepartmentBean && currentDepartmentBean.parentId != accountBean.unitId) {
                    queryDepartment(currentDepartmentBean.id, currentDepartmentBean.parentId, currentDepartmentBean.departmentName, currentDepartmentBean);
                } else {
                    queryDepartment(accountBean.unitId, accountBean.unitId, accountBean.unitName, null);
                }
                break;

            //主题党日
            case R.id.tv_ztdr:
                startOrganizingLifeDetailsActivity(2);
                break;

            //三会一课
            case R.id.tv_shyk:
                startOrganizingLifeDetailsActivity(3);
                break;

            //民主评议
            case R.id.tv_mzpy:
                startOrganizingLifeDetailsActivity(4);
                break;

            //组织生活会
            case R.id.tv_zzshh:
                startOrganizingLifeDetailsActivity(5);
                break;

            //其他
            case R.id.tv_more:
                startOrganizingLifeDetailsActivity(6);
                break;

            case R.id.iv_switch:
                showMap = !showMap;

                switchView();

                //如果不是显示地图，则请求列表数据
                if (!showMap) {
                    //如果不是-1则请求当前的view
                    if (-1 != mUnitId) {
                        queryDepartment(mUnitId, mParentUnitId, mDescriptionName, null);
                    } else {
                        queryDepartment(accountBean.unitId, accountBean.unitId, accountBean.unitName, null);
                    }
                }
                break;

            //地址
            case R.id.tv_address:
//                DialogUtils.showProgressDialog(getActivity(),"正在查询信息...");
//                if (null != currentDepartmentBean && currentDepartmentBean.parentId != accountBean.unitId) {
//                    queryDepartment(currentDepartmentBean.id, currentDepartmentBean.parentId, currentDepartmentBean.departmentName, currentDepartmentBean);
//                } else {
//                    queryDepartment(accountBean.unitId, accountBean.unitId, accountBean.unitIdVal, null);
//                }
                checkAccountBean();
                //查询经纬度
                if (showMap) {
                    queryLonlats(accountBean.unitId, accountBean.unitId, accountBean.unitName, null, true);
                }
                break;

            //信息
            case R.id.tv_info:
                checkAccountBean();
                if (-1 != mUnitId) {
                    //呼叫号码如何传需要重新定义
                    OrganizingLifeDetailsActivity.startActivity(
                            getActivity(),
                            0,
                            mUnitId,
                            mDescriptionName,
                            mDescriptionName,
                            mParentUnitId,
                            "");
                }
                break;

            case R.id.tv_right_operate:
                MineActivity.startActivity(getActivity());
                break;
        }
    }

    public void checkAccountBean() {
        if (null == accountBean) {
            accountBean = PreferenceUtil.getUserInfo(getActivity());
        }
    }

    public void startOrganizingLifeDetailsActivity(int index) {
        checkAccountBean();
        OrganizingLifeDetailsActivity.startActivity(getActivity(),
                index,
                accountBean.unitId,
                accountBean.unitName,
                accountBean.unitName,
                -1,
                "");
    }

    /**
     * 是否显示地图
     */
    private void switchView() {
        flAddress.setVisibility(showMap ? View.GONE : View.VISIBLE);
        flMap.setVisibility(showMap ? View.VISIBLE : View.GONE);

        ivSwitch.setImageResource(showMap ? R.mipmap.ic_switch_list : R.mipmap.ic_switch_map);
    }

    private String TAG = "OrganizingLifeFragment";

    @Override
    public void onDestroyView() {
        Log.i(TAG, TAG + "-->onDestroyView");
        mapViewDestroy();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mapViewDestroy();

        Log.i(TAG, TAG + "-->onDestroy");
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        super.onDestroy();
    }

    public void mapViewDestroy() {
        if (null != mapView) {
            try {
                myLocationOverlay.disableMyLocation();
                myLocationOverlay = null;
                mapView.removeAllOverlay();
                mLayerMnger = null;
                mapView.removeAllViews();
                mapView.removeCache();
                mapView.onDestroy();
                mapView = null;
            } catch (Exception e) {
            }
        }
    }

    /**
     * 查询组织单位
     */
    private void queryDepartment(int currentUnitId,
                                 int parentUnitId,
                                 String departmentName,
                                 DepartmentBean departmentBean) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartment(currentUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DepartmentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DepartmentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            mUnitId = currentUnitId;
                            mParentUnitId = parentUnitId;
                            mDescriptionName = departmentName;

                            //代表是辛集市
                            if (parentUnitId == accountBean.unitId && departmentName.equals(accountBean.unitName)) {
                                tvAddress.setText(accountBean.unitName);
                                ivBack.setVisibility(View.GONE);
                            } else {
                                tvAddress.setText(departmentName);
                                ivBack.setVisibility(View.VISIBLE);
                            }

                            //当前的bean
                            currentDepartmentBean = departmentBean;

                            List<DepartmentBean> dataList = baseData.dataList;

                            if (null != dataList && dataList.size() > 0) {
                                //更新recycler数据
                                updateRecyclerDate(dataList);

                                initIndexBar(dataList);
                            }
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        addressAdapter.setEmptyView(errorView);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 查询经纬度
     */
    private void queryLonlats(int currentUnitId,
                              int parentUnitId,
                              String departmentName,
                              LonlatBean lonlatBean,
                              boolean forceRefresh) {

        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryLonlats(currentUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<LonlatBean>>() {
                    @Override
                    public void onSuccess(BaseData<LonlatBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if (accountBean.unitName.equals(departmentName)) {
                                ivBack.setVisibility(View.GONE);
                            } else {
                                ivBack.setVisibility(View.VISIBLE);
                            }

                            tvAddress.setText(departmentName);

                            mUnitId = currentUnitId;
                            mParentUnitId = parentUnitId;
                            mDescriptionName = departmentName;

                            List<LonlatBean> dataList = baseData.dataList;

                            if (null != dataList && dataList.size() > 0) {
                                if (forceRefresh) {
                                    int childCount = mapView.getChildCount();
                                    View childAtOne = mapView.getChildAt(0);
                                    View childAtTwo = mapView.getChildAt(1);
                                    View childAtThree = mapView.getChildAt(2);

                                    int startIndex = 0;

                                    for (int i = 0; i < mapView.getChildCount(); i++) {
                                        View childView = mapView.getChildAt(i);
                                        if (!(childView instanceof TextView)) {
                                            startIndex++;
                                        }
                                    }

                                    if (allLonlatBeans.size() > 0 && startIndex > 0) {
                                        mapView.removeViews(startIndex, allLonlatBeans.size());
                                    }

                                    allLonlatBeans.clear();
                                }

                                boolean contains = allLonlatBeans.containsAll(dataList);

                                if (!contains) {
                                    allLonlatBeans.addAll(dataList);
                                    //初始化map
                                    drawOverlay(dataList, lonlatBean, forceRefresh);
                                }
                            }
                        } else {
                            if (forceRefresh) {
                                setCenter(Double.valueOf(accountBean.latitude), Double.valueOf(accountBean.longitude));
                            }
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        addressAdapter.setEmptyView(errorView);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private double mLatitude;
    private double mLongitude;

    class MyOverlay extends MyLocationOverlay {
        public MyOverlay(Context context, MapView mapView) {
            super(context, mapView);
        }

        /*
         * 处理在"我的位置"上的点击事件
         */
        @Override
        protected boolean dispatchTap() {
            return false;
        }

        @Override
        public void onLocationChanged(Location location) {
            super.onLocationChanged(location);

            if (null != myLocationOverlay) {
                if (null != location && location.getLatitude() > 0 && location.getLongitude() > 0) {
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                }
            }
        }
    }

    /**
     * 是否添加了本地图片
     */
    private boolean isAddLocal = false;


    private Subscription confInfoSubscribe;

    /**
     * 停止查询数据
     */
    private void stopConfInfoSubscribe() {
        if (null != confInfoSubscribe) {
            if (!confInfoSubscribe.isUnsubscribed()) {
                confInfoSubscribe.unsubscribe();
                confInfoSubscribe = null;
            }
        }
    }

    /**
     * 查询会议是否有数据
     * 循环查询会议信息
     */
    private void checkAccount() {
        stopConfInfoSubscribe();

        confInfoSubscribe = Observable
                .interval(3, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<BaseData>>() {
                    @Override
                    public Observable<BaseData> call(Long aLong) {
                        return HttpUtils.getInstance(getActivity())
                                .getRetofitClinet()
                                .setBaseUrl(Urls.BASE_URL)
                                .builder(StudyApi.class)
                                .addLonlat(String.valueOf(mLongitude), String.valueOf(mLatitude));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        CommonLogger.i("checkAccount", "出异常了");
                        checkAccount();
                    }

                    @Override
                    public void onSuccess(BaseData baseData) {
                        Logger.i("checkAccount", baseData.toString());
                        if (baseData.message.contains("当前操作员绑定会场不在会议中")) {
//                            stopConfInfoSubscribe();
                        }
                    }
                });
    }

    private String[] mActions = new String[]{
            CustomBroadcastConstants.GET_CONF_END,
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.LOGIN_SUCCESS,
            CustomBroadcastConstants.LOGIN_FAILED
    };


    /**
     * 监听广播
     *
     * @param broadcastName
     * @param obj
     */
    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.GET_CONF_END:
                break;

            case CustomBroadcastConstants.ACTION_CALL_END:
                isAddLocal = false;
                stopConfInfoSubscribe();
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                Log.i("OrganizingLifeFragment", "CustomBroadcastConstants.ADD_LOCAL_VIEW" + System.currentTimeMillis());
                if (!isAddLocal) {
                    isAddLocal = true;
                    checkAccount();
                }
                break;

            case CustomBroadcastConstants.LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");

                LoginCenter.getInstance().getSipAccountInfo().setSiteName(PreferencesHelper.getData(UIConstants.SITE_NAME));
                //是否登录
                PreferencesHelper.saveData(UIConstants.IS_LOGIN, true);
                PreferencesHelper.saveData(UIConstants.REGISTER_RESULT, "0");
                ToastHelper.showShort("华为平台登录成功");
                break;

            case CustomBroadcastConstants.LOGIN_FAILED:
                String errorMessage = ((String) obj);
                LogUtil.i(UIConstants.DEMO_TAG, "login failed," + errorMessage);

                ToastHelper.showShort("华为平台登录失败" + errorMessage);
                retryCount++;
                if (retryCount <= 3) {
                    //登录华为平台
                    HuaweiLoginImp.getInstance().loginRequest(getActivity(), accountBean.siteAccount, accountBean.sitePwd, Urls.SMC_REGISTER_SERVER, Urls.SMC_REGISTER_PORT, "", "", "", "");
                }
                break;

            case CustomBroadcastConstants.LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success");
                break;
        }
    }


    //华为平台登录重试次数
    private int retryCount = 0;
}
