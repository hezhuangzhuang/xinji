package com.zxwl.xinji.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.ToastHelper;
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
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.xinji.activity.MapDetailsActivity;
import com.zxwl.xinji.activity.MineActivity;
import com.zxwl.xinji.activity.PoiKeywordSearchActivity;
import com.zxwl.xinji.adapter.AddressNewAdapter;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PinyinUtils;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qdx.stickyheaderdecoration.NormalDecoration;

/**
 * 党建地图
 */
public class PartyMapNewFragment extends BaseLazyFragment implements View.OnClickListener {
    private String TAG = "OrganizingLifeFragment";

    private LoginBean.AccountBean accountBean;

    private WeakReference<Activity> weakReference;

    private TextView tvJjxx;
    private TextView tvCjzz;
    private TextView tvDjzd;
    private TextView tvTsbc;
    private TextView tvMore;
    private TextView tvTopTitle;
    private TextView tvRightOperate;

    private RelativeLayout rlTopTitle;

    private MapView mapView;
    private AMap aMap;

    private ImageView ivBack;
    private TextView tvAddress;
    private ImageView ivSwitch;
    private TextView tvInfo;
    private TextView tvSearchAddress;

    private RelativeLayout rlAddress;

    //当前单位父id
    private int mParentUnitId = -1;
    //当前单位的id
    private int mUnitId = -1;

    //村庄名
    private String mDescriptionName;

    private List<LonlatBean> allLonlatBeans;

    //所有村子的点
    private HashMap<String, List<LonlatBean>> allLoglatMap = new HashMap<>();

    //村子的点
    private String regionPoints;

    //地图创建时需要使用
    private Bundle savedInstanceState;

    private LonlatBean currentLonlatBean;

    private FrameLayout flList;
    private RecyclerView rvList;
    private FrameLayout flMap;

    private AddressNewAdapter addressAdapter;
    private NormalDecoration decoration;

    //是否显示地图
    private boolean showMap = true;

    public PartyMapNewFragment() {
    }

    public static PartyMapNewFragment newInstance() {
        PartyMapNewFragment fragment = new PartyMapNewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_party_map_new, container, false);
    }

    @Override
    protected void findViews(View view) {
        tvJjxx = (TextView) view.findViewById(R.id.tv_jjxx);
        tvCjzz = (TextView) view.findViewById(R.id.tv_cjzz);
        tvDjzd = (TextView) view.findViewById(R.id.tv_djzd);
        tvTsbc = (TextView) view.findViewById(R.id.tv_tsbc);
        tvMore = (TextView) view.findViewById(R.id.tv_more);
        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) view.findViewById(R.id.tv_right_operate);
        rlTopTitle = (RelativeLayout) view.findViewById(R.id.rl_top_title);

        mapView = (MapView) view.findViewById(R.id.map);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        tvAddress = (TextView) view.findViewById(R.id.tv_address);
        ivSwitch = (ImageView) view.findViewById(R.id.iv_switch);
        tvInfo = (TextView) view.findViewById(R.id.tv_info);
        tvSearchAddress = (TextView) view.findViewById(R.id.tv_search_address);

        rlAddress = (RelativeLayout) view.findViewById(R.id.rl_address);

        flList = (FrameLayout) view.findViewById(R.id.fl_list);
        rvList = (RecyclerView) view.findViewById(R.id.rl_list);
        flMap = (FrameLayout) view.findViewById(R.id.fl_map);
    }

    @Override
    protected void addListeners() {
        tvSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoiKeywordSearchActivity.startActivity(getActivity());
            }
        });
        tvRightOperate.setOnClickListener(this);
        tvJjxx.setOnClickListener(this);
        tvCjzz.setOnClickListener(this);
        tvDjzd.setOnClickListener(this);
        tvTsbc.setOnClickListener(this);
        tvMore.setOnClickListener(this);

        ivBack.setOnClickListener(this);
        tvInfo.setOnClickListener(this);
        ivSwitch.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("党建地图");
        //如果用户信息不为空则请求数据
        weakReference = new WeakReference<Activity>(getActivity());

        //初始化map
        initMap();

        if (isLogin() && null != PreferenceUtil.getUserInfo(getActivity())) {
            rlAddress.setVisibility(View.VISIBLE);
            accountBean = PreferenceUtil.getUserInfo(getActivity());

            allLonlatBeans = new ArrayList<>();
//            //初始化map
//            initMap();
            tvAddress.setText(accountBean.unitName);

            //查询经纬度
            queryLonlats(accountBean.unitId, accountBean.unitId, accountBean.unitName, null, true);

            //初始化适配器
            initAddressAdapter();
        } else {
            rlAddress.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化适配器
     */
    @NonNull
    private void initAddressAdapter() {
        addressAdapter = new AddressNewAdapter(R.layout.item_address, new ArrayList<>());

        addressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LonlatBean departmentBean = addressAdapter.getItem(position);
                //判断是否是父
                if (3 != departmentBean.areaType) {
                    currentLonlatBean = departmentBean;
                    //查询子列表
                    queryLonlatsList(departmentBean.departmentId, departmentBean.departmentName);
                } else {
                    DepartmentBean item = new DepartmentBean();
                    item.departmentName = departmentBean.departmentName;
                    item.terUri = departmentBean.siteUri;
                    item.id = departmentBean.departmentId;
                    item.parentId = departmentBean.parentId;
                    MapDetailsActivity.startActivity(getActivity(),
                            0,
                            departmentBean.departmentId,
                            item,
                            true);
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(addressAdapter);
    }

    /**
     * 绘制市的范围
     */
    private void drawPolygon() {
        regionPoints = getString(R.string.region);

        String[] strings = regionPoints.split(",");
        ArrayList<LatLng> points = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            String[] lon = string.split(" ");

            points.add(new LatLng(Double.valueOf(lon[1]), (Double.valueOf(lon[0]))));
        }

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(points);
        polygonOptions.fillColor(ContextCompat.getColor(getActivity(), R.color.color_f5f0e7));
        polygonOptions.strokeWidth(5);
        polygonOptions.strokeColor(ContextCompat.getColor(getActivity(), R.color.color_facd8f));
        aMap.addPolygon(polygonOptions);
    }

    private void initMap() {
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
            aMap.showMapText(false);
            setUpMap();
        }

        //绘制市的范围
//        drawPolygon();
    }

    private void setUpMap() {
        //指南针是否显示
        aMap.getUiSettings().setCompassEnabled(false);

        //旋转手势是否可用
        aMap.getUiSettings().setRotateGesturesEnabled(false);

        aMap.getUiSettings().setZoomControlsEnabled(false);
        // 设置marker可拖拽事件监听器
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });
        // 设置amap加载成功事件监听器
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {

            }
        });
        // 设置点击marker事件监听器
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //获取点击的lonlatbean
                MarkerOptions options = marker.getOptions();
                LonlatBean lonlatBean = hashMapMarkerLonlat.get(options);

                //如果级别是街村
                if (3 != lonlatBean.areaType) {
                    //获取当前的地点
                    currentLonlatBean = lonlatBean;
                    //查询
                    queryLonlats(lonlatBean.departmentId, lonlatBean.parentId, lonlatBean.departmentName, lonlatBean, false);
                } else {
                    DepartmentBean departmentBean = new DepartmentBean();
                    departmentBean.departmentName = lonlatBean.departmentName;
                    departmentBean.terUri = lonlatBean.siteUri;
                    departmentBean.parentId = lonlatBean.parentId;

                    MapDetailsActivity.startActivity(getActivity(),
                            0,
                            lonlatBean.departmentId,
                            departmentBean,
                            true);
                }
                return false;
            }
        });
        // 设置点击infoWindow事件监听器
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });
    }

    /**
     * 绘制市的范围
     */
    private void drawOverlay(List<LonlatBean> lonlatBeans, LonlatBean newLonlatBean, boolean forceRefresh) {
        //创建覆盖物
        initMarker(lonlatBeans);

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
            float zoomLevel = aMap.getCameraPosition().zoom;
            setZoom(zoomLevel + 1);
        } else {
            if (lonlatBeans.size() > 0) {
                int level = getLevel();
                setZoom(level);
            }
        }
    }

    private TextView mPopView;
    private LatLng latLng;

    private MarkerOptions markerOption;
    private ArrayList<MarkerOptions> markerOptionlst;

    private HashMap<MarkerOptions, LonlatBean> hashMapMarkerLonlat = new HashMap<>();

    private void initMarker(List<LonlatBean> lonlatBeans) {
        markerOptionlst = new ArrayList<MarkerOptions>();

        for (LonlatBean lonlatBean : lonlatBeans) {
            latLng = new LatLng(lonlatBean.latitude, lonlatBean.longitude);
            mPopView = (TextView) getLayoutInflater().inflate(R.layout.map_popview, null);
            mPopView.setText(lonlatBean.departmentName);
            mPopView.setTag(lonlatBean);
            markerOption = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromView(mPopView))
                    .draggable(true)
                    .period(10);

            markerOptionlst.add(markerOption);

            //存到map中，在点击的时候取出来
            hashMapMarkerLonlat.put(markerOption, lonlatBean);
        }

        aMap.addMarkers(markerOptionlst, true);
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
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10));
    }

    /**
     * 设置缩放比例
     *
     * @param zoomLevel
     */
    private void setZoom(float zoomLevel) {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
    }

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    public void onClick(View v) {
        if (!isLogin()) {
            ToastHelper.showShort("请登录后操作");
            LoginActivity.startActivity(getActivity());
            return;
        }

        switch (v.getId()) {
            case R.id.tv_right_operate:
                MineActivity.startActivity(getActivity());
                break;

            case R.id.tv_jjxx:
                MapDetailsActivity.startActivity(getActivity(), 0, accountBean.unitId, null);
                break;

            case R.id.tv_cjzz:
                MapDetailsActivity.startActivity(getActivity(), 1, accountBean.unitId, null);
                break;

            case R.id.tv_djzd:
                MapDetailsActivity.startActivity(getActivity(), 2, accountBean.unitId, null);
                break;

            case R.id.tv_tsbc:
                MapDetailsActivity.startActivity(getActivity(), 3, accountBean.unitId, null);
                break;

            case R.id.tv_more:
                MapDetailsActivity.startActivity(getActivity(), 4, accountBean.unitId, null);
                break;


            case R.id.tv_info:
                if (accountBean.unitName.equals(tvAddress.getText().toString().trim())) {
                    MapDetailsActivity.startActivity(getActivity(), 0, accountBean.unitId, null);
                } else {
                    DepartmentBean departmentBean = new DepartmentBean();
                    departmentBean.departmentName = currentLonlatBean.departmentName;
                    departmentBean.terUri = currentLonlatBean.siteUri;
                    departmentBean.id = currentLonlatBean.departmentId;
                    departmentBean.parentId = currentLonlatBean.parentId;

                    MapDetailsActivity.startActivity(getActivity(),
                            0,
                            currentLonlatBean.departmentId,
                            departmentBean);
                }
                break;

            //返回上一级
            case R.id.iv_back:
                //查询经纬度
                queryLonlats(accountBean.unitId, accountBean.unitId, accountBean.unitName, null, true);
                queryLonlatsList(accountBean.unitId, accountBean.unitName);
                break;

            //地址
            case R.id.tv_address:
                //查询经纬度
                queryLonlats(accountBean.unitId, accountBean.unitId, accountBean.unitName, null, true);
                queryLonlatsList(accountBean.unitId, accountBean.unitName);
                break;

            //切换显示
            case R.id.iv_switch:
                showMap = !showMap;
                if (showMap) {
                    flMap.setVisibility(View.VISIBLE);
                    flList.setVisibility(View.GONE);
                } else {
                    flList.setVisibility(View.VISIBLE);
                    flMap.setVisibility(View.GONE);
                    //查询当前的列表
                    if (accountBean.unitName.equals(tvAddress.getText().toString().trim())) {
                        queryLonlatsList(accountBean.unitId, accountBean.unitName);
                    } else {
                        queryLonlatsList(currentLonlatBean.departmentId, currentLonlatBean.departmentName);
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, TAG + "-->onDestroyView");
        super.onDestroyView();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
//        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        gaodeMap.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁时，需要销毁定位client
    }

    /**
     * 村和乡镇的对应关系
     */
    private HashMap<String, List<LonlatBean>> hashMapLonlat = new HashMap<>();

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

                            //如果是当前登陆账号就清空标记
                            if (currentUnitId == accountBean.unitId) {
                                //清空标记
                                hashMapLonlat.clear();
                                //清空标记
                                aMap.clear();
                            }

                            tvAddress.setText(departmentName);

                            mUnitId = currentUnitId;
                            mParentUnitId = parentUnitId;
                            mDescriptionName = departmentName;

                            List<LonlatBean> dataList = baseData.dataList;

                            if (null != dataList && dataList.size() > 0) {
                                //判断当前key是否存在
                                boolean contains = hashMapLonlat.containsKey(departmentName);

                                //判断是否包含
                                if (!contains) {
                                    hashMapLonlat.put(departmentName, dataList);
                                    //初始化map
                                    drawOverlay(dataList, lonlatBean, forceRefresh);
                                }

                                if (forceRefresh) {
                                    //绘制市的范围
                                    drawPolygon();
                                    //shezhi1
                                    setCenter(Double.valueOf(accountBean.latitude), Double.valueOf(accountBean.longitude));
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
//                        addressAdapter.setEmptyView(errorView);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 查询列表
     */
    private void queryLonlatsList(int currentUnitId,
                                  String departmentName) {
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
                            if (null != baseData.dataList && baseData.dataList.size() > 0) {
                                updateRecyclerDate(baseData.dataList);
                            }
                            tvAddress.setText(departmentName);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
//                        addressAdapter.setEmptyView(errorView);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private void updateRecyclerDate(List<LonlatBean> selectData) {
        addressAdapter.replaceData(selectData);

        if (null != decoration) {
            //移除之前的decoration
            decoration.onDestory();
            rvList.removeItemDecorationAt(0);
        }

        decoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                return PinyinUtils.getFirstPinYinHeadChar(selectData.get(pos).departmentName);
            }
        };

        rvList.addItemDecoration(decoration);

        rvList.smoothScrollToPosition(0);
    }

}
