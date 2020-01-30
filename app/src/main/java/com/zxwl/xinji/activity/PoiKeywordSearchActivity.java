package com.zxwl.xinji.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapRouteActivity;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapCarInfo;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.KeyBoardUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.map.AMapUtil;
import com.zxwl.xinji.utils.map.overlay.PoiOverlay;
import com.zxwl.xinji.widget.MapAddressDialog;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;
import razerdp.basepopup.BasePopupWindow;


/**
 * AMapV1地图中简单介绍poisearch搜索
 * 搜索定位界面
 */
public class PoiKeywordSearchActivity extends BaseActivity implements OnClickListener, CancelAdapt {
    private String local_City = "武汉市";

    private LinearLayout ll_search;

    //导航对象(单例)
    private AMapNavi mAMapNavi;
    private AMap aMap;

    // 输入搜索关键字
    private AutoCompleteTextView searchText;
    // 要输入的poi搜索关键字
    private String keyWord = "";
    // 搜索时进度条
    private ProgressDialog progDialog = null;
    //要输入的城市名字或者城市区号
    private EditText editCity;//
    // poi返回的结果
    private PoiResult poiResult;
    // 当前页面，从0开始计数
    private int currentPage = 0;
    // Poi查询条件类
    private PoiSearch.Query query;
    // POI搜索
    private PoiSearch poiSearch;

    private ImageView ivBack;
    private ImageView ivSearch;

    /**
     * 搜索线路的布局-start
     */
//    private ConstraintLayout clLine;
//    private TextView tvLineDrive;
//    private TextView tvLineWalk;
//    private ImageView ivSwitvh;
//    private TextView tvStart;
//    private TextView tvEnd;
//    private FrameLayout flLineNavi;
    /*搜索线路的布局-end***********************************/

    /**
     * 搜索线路的布局-start
     */
    private RelativeLayout rlAddress;
    private TextView tvTitle;
    private TextView tvSnippet;
    private FrameLayout flLine;
    /*搜索线路的布局-end***********************************/

    //当前搜索的地点
    private PoiItem currentPoiItem;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PoiKeywordSearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivSearch = (ImageView) findViewById(R.id.iv_search);

        ll_search = findViewById(R.id.ll_search);

        rlAddress = (RelativeLayout) findViewById(R.id.rl_address);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSnippet = (TextView) findViewById(R.id.tv_snippet);
        flLine = (FrameLayout) findViewById(R.id.fl_line);

//        clLine = (ConstraintLayout) findViewById(R.id.cl_line);
//        tvLineDrive = (TextView) findViewById(R.id.tv_line_drive);
//        tvLineWalk = (TextView) findViewById(R.id.tv_line_walk);
//        ivSwitvh = (ImageView) findViewById(R.id.iv_switvh);
//        tvStart = (TextView) findViewById(R.id.tv_start);
//        tvEnd = (TextView) findViewById(R.id.tv_end);
//        flLineNavi = (FrameLayout) findViewById(R.id.fl_line_navi);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setTranslucentForImageView(this, 0, ll_search);

        init();

        setMapLocation();

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(aMapNaviListener);
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        flLine.setOnClickListener(this);
//        tvLineDrive.setOnClickListener(this);
//        tvLineWalk.setOnClickListener(this);
//        ivSwitvh.setOnClickListener(this);
//        tvStart.setOnClickListener(this);
//        tvEnd.setOnClickListener(this);
//        flLineNavi.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.poikeywordsearch_activity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        routeOverlays.clear();
        /**
         * 当前页面只是展示地图，activity销毁后不需要再回调导航的状态
         */
        mAMapNavi.removeAMapNaviListener(aMapNaviListener);
        mAMapNavi.destroy();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            setUpMap();
        }
    }

    private MyLocationStyle myLocationStyle;

    /**
     * 设置定位到当前位置
     */
    private void setMapLocation() {
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();

        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //myLocationStyle.interval(2000);

        //连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);

        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);

        //设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(false);

        //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);

        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    /**
     * 设置页面监听
     */
    private void setUpMap() {
        Button searButton = (Button) findViewById(R.id.searchButton);
        searButton.setOnClickListener(this);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();
                if (!AMapUtil.IsEmptyOrNullString(newText)) {
                    InputtipsQuery inputquery = new InputtipsQuery(newText, local_City);
                    Inputtips inputTips = new Inputtips(PoiKeywordSearchActivity.this, inputquery);
                    inputTips.setInputtipsListener(new InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> tipList, int rCode) {
                            if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
                                List<String> listString = new ArrayList<String>();
                                for (int i = 0; i < tipList.size(); i++) {
                                    listString.add(tipList.get(i).getName());
                                }
                                ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.route_inputs, listString);
                                searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        searchKeyWork();
                                    }
                                });
                                searchText.setAdapter(aAdapter);
                                aAdapter.notifyDataSetChanged();
                            } else {
                                ToastHelper.showShort("rCode" + rCode);
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });// 添加文本输入框监听事件
        editCity = (EditText) findViewById(R.id.city);

        // 添加点击marker监听事件
        aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

        // 添加显示infowindow监听事件
//        aMap.setInfoWindowAdapter(new InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
//                        null);
//
//                TextView title = (TextView) view.findViewById(R.id.title);
//                title.setText(marker.getTitle());
//
//                TextView snippet = (TextView) view.findViewById(R.id.snippet);
//                snippet.setText(marker.getSnippet());
//                ImageButton button = (ImageButton) view.findViewById(R.id.start_amap_app);
//
//                // 调起高德地图app
//                button.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startAMapNavi(marker);
//                    }
//                });
//
//                return view;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                return null;
//            }
//        });
    }

    /**
     * 点击搜索按钮
     */
    public void searchKeyWork() {
        keyWord = AMapUtil.checkEditText(searchText);
        if (TextUtils.isEmpty(keyWord)) {
            ToastHelper.showShort("请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }

    /**
     * 点击下一页按钮
     */
    public void nextButton() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() - 1 > currentPage) {
                currentPage++;
                query.setPageNum(currentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {
                ToastHelper.showShort("没有结果了");
            }
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        showProgressDialog();// 显示进度框
        currentPage = 0;
        // 第一个参数表示搜索字符串，
        // 第二个参数表示poi搜索类型，
        // 第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyWord, "", local_City);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(10);
        // 设置查第一页
        query.setPageNum(currentPage);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int rCode) {
                //隐藏对话框
                dissmissProgressDialog();
                //关闭键盘
                KeyBoardUtil.closeKeybord(searchText, searchText.getContext());

                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    // 搜索poi的结果
                    if (result != null && result.getQuery() != null) {
                        // 是否是同一条
                        if (result.getQuery().equals(query)) {
                            poiResult = result;
                            // 取得搜索到的poiitems有多少页
                            List<PoiItem> poiItems = poiResult.getPois();
                            // 取得第一页的poiitem数据，页数从数字0开始
                            List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                            List<PoiItem> newPoi = new ArrayList<>();
                            for (PoiItem poiItem : poiItems) {
                                if (poiItem.getTitle().equals(searchText.getText().toString())) {
                                    newPoi.add(poiItem);
                                }
                            }

//                          if (poiItems != null && poiItems.size() > 0) {
                            if (poiItems != null && newPoi.size() > 0) {
                                aMap.clear();// 清理之前的图标
                                //PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                                PoiOverlay poiOverlay = new PoiOverlay(aMap, newPoi);
                                poiOverlay.removeFromMap();
                                poiOverlay.addToMap();
                                poiOverlay.zoomToSpan();

                                try {
                                    showAddress(newPoi.get(0));
                                } catch (Exception e) {
                                    ToastHelper.showShort(e.getMessage());
                                }


                                //显示底部对话框
//                                initAddressDialog(newPoi.get(0));
                            } else if (suggestionCities != null
                                    && suggestionCities.size() > 0) {
                                //poi没有搜索到数据，返回一些推荐城市的信息
                                showSuggestCity(suggestionCities);
                            } else {
                                ToastHelper.showShort("没有结果");
                            }
                        }
                    } else {
                        ToastHelper.showShort("没有结果");
                    }
                } else {
                    ToastHelper.showShort("rCode：" + rCode);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    private void showAddress(PoiItem poiItem) {
        currentPoiItem = poiItem;
        //两点间的距离
        float distance = AMapUtils.calculateLineDistance(
                //当前位置
                new LatLng(
                        aMap.getMyLocation().getLatitude(),
                        aMap.getMyLocation().getLongitude()),
                //搜索的位置
                new LatLng(
                        poiItem.getLatLonPoint().getLatitude(),
                        poiItem.getLatLonPoint().getLongitude()
                )
        );
        rlAddress.setVisibility(View.VISIBLE);
        tvTitle.setText(poiItem.getTitle());
        tvSnippet.setText("距您" + AMapUtil.getFriendlyLength((int) distance) + " | " + poiItem.getSnippet());
    }

    /**
     * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
     */
    public void startAMapNavi(Marker marker) {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(marker.getPosition());
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);

        // 调起高德地图导航
        try {
            AMapUtils.openAMapNavi(naviPara, getApplicationContext());
        } catch (com.amap.api.maps.AMapException e) {
            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(getApplicationContext());
        }
    }

    /**
     * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
     */
    public void startAMapNavi(PoiItem endPoiItem, AmapNaviType naviType) {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(new LatLng(
                endPoiItem.getLatLonPoint().getLatitude(),
                endPoiItem.getLatLonPoint().getLongitude()));
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);

        // 调起高德地图导航
        try {
            AMapUtils.openAMapNavi(naviPara, getApplicationContext());
        } catch (com.amap.api.maps.AMapException e) {
            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(getApplicationContext());

            //修改为没安装就调用我们当前程序的导航界面
            startLocalNav(null, getPoi(endPoiItem), naviType);
        }
    }

    @NonNull
    public Poi getPoi(PoiItem endPoiItem) {
        return new Poi(
                endPoiItem.getTitle(),
                new LatLng(endPoiItem.getLatLonPoint().getLatitude(), endPoiItem.getLatLonPoint().getLongitude()),
                "");
    }

    /**
     * 判断高德地图app是否已经安装
     */
    public boolean getAppIn() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(
                    "com.autonavi.minimap", 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        // 本手机没有安装高德地图app
        if (packageInfo != null) {
            return true;
        }
        // 本手机成功安装有高德地图app
        else {
            return false;
        }
    }

    /**
     * 获取当前app的应用名字
     */
    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    getPackageName(), 0);
        } catch (NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager
                .getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastHelper.showShort(infomation);
    }


    /**
     * Button点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击搜索按钮
             */
            case R.id.searchButton:
            case R.id.iv_search:
                searchKeyWork();
                break;

            /**
             * 点击下一页按钮
             */
            case R.id.nextButton:
                nextButton();
                break;

            //关闭界面
            case R.id.iv_back:
                finish();
                break;

            //开始导航
            case R.id.fl_line:
                //开始导航
                startAMapNavi(currentPoiItem, AmapNaviType.DRIVER);
                break;

            default:
                break;
        }
    }

    //驾车
    private boolean isDrive = true;


    /**
     * 显示地点对话框-start
     *********************************************************************/
    private MapAddressDialog addressDialog;

    /**
     * 初始化地点对话框
     *
     * @param poiItem
     */
    private void initAddressDialog(PoiItem poiItem) {
        if (null == addressDialog) {
            addressDialog = new MapAddressDialog(this, BasePopupWindow.MATCH_PARENT, BasePopupWindow.WRAP_CONTENT, poiItem);
            addressDialog.setAddressClickListener(new MapAddressDialog.onAddressClickListener() {
                @Override
                public void onNavi(PoiItem poiItem) {
                }

                @Override
                public void onLine(PoiItem endPoiItem) {
                    startLatlng = new NaviLatLng(
                            aMap.getMyLocation().getLatitude(),
                            aMap.getMyLocation().getLongitude());

                    endLatlng = new NaviLatLng(endPoiItem.getLatLonPoint().getLatitude(),
                            endPoiItem.getLatLonPoint().getLongitude());

                    startLatlngList.clear();
                    startLatlngList.add(startLatlng);

                    endLatlngList.clear();
                    endLatlngList.add(endLatlng);

                    //开始计算线路
//                    startCalculationDriveRoute();

                    //隐藏对话框
                    addressDialog.dismiss();

//                    clLine.setVisibility(View.VISIBLE);
//                    tvStart.setText("我的位置");
//                    tvEnd.setText(poiItem.getTitle());
                }
            });
        } else {
            addressDialog.setPoiItem(poiItem);
        }

        //两点间的距离
        float distance = AMapUtils.calculateLineDistance(
                //当前位置
                new LatLng(
                        aMap.getMyLocation().getLatitude(),
                        aMap.getMyLocation().getLongitude()),
                //搜索的位置
                new LatLng(
                        poiItem.getLatLonPoint().getLatitude(),
                        poiItem.getLatLonPoint().getLongitude()
                )
        );

        addressDialog.setDistance(distance);

        addressDialog.setBackgroundColor(ContextCompat.getColor(this, R.color.tran));
        addressDialog.setAlignBackground(true);
        addressDialog.setPopupGravity(Gravity.BOTTOM);

        //是否允许点击PopupWindow外部时触发dismiss
        addressDialog.showPopupWindow();
    }
    /**
     * 初始化地点对话框-end
     *********************************************************************/


    /**
     * 跳转到应用自带的导航界面-start
     *
     * @param startPoi
     * @param endPoi
     *******************************************************/
    private void startLocalNav(Poi startPoi, Poi endPoi, AmapNaviType naviType) {
        AmapNaviParams params = new AmapNaviParams(startPoi, null, endPoi, naviType);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(this, params, iNaviInfoCallback, AmapRouteActivity.class);
    }

    private INaviInfoCallback iNaviInfoCallback = new INaviInfoCallback() {
        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onArriveDestination(boolean b) {

        }

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {

        }

        @Override
        public void onCalculateRouteFailure(int i) {
        }

        @Override
        public void onStopSpeaking() {

        }

        @Override
        public void onReCalculateRoute(int i) {

        }

        @Override
        public void onExitPage(int i) {

        }

        @Override
        public void onStrategyChanged(int i) {

        }

        @Override
        public View getCustomNaviBottomView() {
            //返回null则不显示自定义区域
//                return getCustomView("底部自定义区域");
            return null;
        }

        @Override
        public View getCustomNaviView() {
            //返回null则不显示自定义区域
//                return getCustomView("中部自定义区域");
            return null;
        }

        @Override
        public void onArrivedWayPoint(int i) {
        }

        @Override
        public void onMapTypeChanged(int i) {
        }

        @Override
        public View getCustomMiddleView() {
            return null;
        }

        @Override
        public void onNaviDirectionChanged(int i) {
        }

        @Override
        public void onDayAndNightModeChanged(int i) {
        }

        @Override
        public void onBroadcastModeChanged(int i) {
        }

        @Override
        public void onScaleAutoChanged(boolean b) {
        }
    };
    /**
     * 跳转到应用自带的导航界面-end
     *******************************************************/


    /**
     * 计算路线-start
     *******************************************************/
    //起点标记
    private Marker mStartMarker;
    //终点标记
    private Marker mEndMarker;

    private NaviLatLng startLatlng;
    private NaviLatLng endLatlng;

    private boolean congestion, cost, hightspeed, avoidhightspeed;

    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();

    /**
     * 路线计算成功标志位
     */
    private boolean calculateSuccess = false;

    /**
     * 开始点集合
     */
    private List<NaviLatLng> startLatlngList = new ArrayList<NaviLatLng>();
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();

    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endLatlngList = new ArrayList<NaviLatLng>();

    /**
     * 绘制路线
     *
     * @param routeId
     * @param path
     */
    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    /**
     * 清除当前地图上算好的路线
     */
    private void clearRoute() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            RouteOverLay routeOverlay = routeOverlays.valueAt(i);
            routeOverlay.removeFromMap();
        }
        routeOverlays.clear();
    }

    /**
     * 开始计算驾车线路
     */
    public void startCalculationDriveRoute() {
        clearRoute();
        if (avoidhightspeed && hightspeed) {
            Toast.makeText(getApplicationContext(), "不走高速与高速优先不能同时为true.", Toast.LENGTH_LONG).show();
        }
        if (cost && hightspeed) {
            Toast.makeText(getApplicationContext(), "高速优先与避免收费不能同时为true.", Toast.LENGTH_LONG).show();
        }
        /*
         * strategyFlag转换出来的值都对应PathPlanningStrategy常量，用户也可以直接传入PathPlanningStrategy常量进行算路。
         * 如:mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,PathPlanningStrategy.DRIVING_DEFAULT);
         */
        int strategyFlag = 0;
        try {
            strategyFlag = mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (strategyFlag >= 0) {
            AMapCarInfo carInfo = new AMapCarInfo();
            //carInfo.setCarNumber(carNumber);
            //设置车牌是否参与限行算路
            //carInfo.setRestriction(true);
            //mAMapNavi.setCarInfo(carInfo);
            mAMapNavi.calculateDriveRoute(startLatlngList, endLatlngList, wayList, strategyFlag);
            ToastHelper.showShort("策略:" + strategyFlag);
        }
    }

    /**
     * 开始计算步行线路
     */
    public void startCalculationWalkRoute() {
        clearRoute();

        boolean b = mAMapNavi.calculateWalkRoute(startLatlng, endLatlng);
        ToastHelper.showShort("策略:" + b);
    }

    private AMapNaviListener aMapNaviListener = new AMapNaviListener() {
        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onInitNaviSuccess() {

        }

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onTrafficStatusUpdate() {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onGetNavigationText(int i, String s) {

        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onEndEmulatorNavi() {

        }

        @Override
        public void onArriveDestination() {

        }

        @Override
        public void onCalculateRouteFailure(int i) {
            calculateSuccess = false;
            ToastHelper.showShort("计算路线失败，errorcode＝" + i);
        }

        @Override
        public void onReCalculateRouteForYaw() {

        }

        @Override
        public void onReCalculateRouteForTrafficJam() {

        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onGpsOpenStatus(boolean b) {

        }

        @Override
        public void onNaviInfoUpdate(NaviInfo naviInfo) {

        }

        @Override
        public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

        }

        @Override
        public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

        }

        @Override
        public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

        }

        @Override
        public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

        }

        @Override
        public void showCross(AMapNaviCross aMapNaviCross) {

        }

        @Override
        public void hideCross() {

        }

        @Override
        public void showModeCross(AMapModelCross aMapModelCross) {

        }

        @Override
        public void hideModeCross() {

        }

        @Override
        public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

        }

        @Override
        public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

        }

        @Override
        public void hideLaneInfo() {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {
            //清空上次计算的路径列表。
//            routeOverlays.clear();
//            HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
//            for (int i = 0; i < ints.length; i++) {
//                AMapNaviPath path = paths.get(ints[i]);
//                if (path != null) {
//                    drawRoutes(ints[i], path);
//                }
//            }
        }


        @Override
        public void notifyParallelRoad(int i) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

        }

        @Override
        public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

        }

        @Override
        public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

        }

        @Override
        public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

        }

        @Override
        public void onPlayRing(int i) {

        }

        @Override
        public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

        }

        @Override
        public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

        }

        @Override
        public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

        }
    };
    /**
     * 计算路线-end
     *******************************************************************/
}
