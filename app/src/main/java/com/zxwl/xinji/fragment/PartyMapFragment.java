package com.zxwl.xinji.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.xinji.activity.MineActivity;
import com.zxwl.xinji.activity.PoiKeywordSearchActivity;
import com.zxwl.xinji.activity.VillageBaseInfoActivity;
import com.zxwl.xinji.activity.VillageListActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 党建地图
 */
public class PartyMapFragment extends BaseLazyFragment implements View.OnClickListener {
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
    private TextView tvSearchAddress;

    private RelativeLayout rlTopTitle;

    public PartyMapFragment() {
        // Required empty public constructor
    }

    public static PartyMapFragment newInstance() {
        PartyMapFragment fragment = new PartyMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //地图创建时需要使用
    private Bundle savedInstanceState;

    private MapView gaodeMap;
    private AMap aMap;

    private MyLocationStyle myLocationStyle;

    //村子的点
    private String regionPoints;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_party_map, container, false);
    }

    @Override
    protected void findViews(View view) {
        gaodeMap = view.findViewById(R.id.map);
        tvJjxx = (TextView) view.findViewById(R.id.tv_jjxx);
        tvCjzz = (TextView) view.findViewById(R.id.tv_cjzz);
        tvDjzd = (TextView) view.findViewById(R.id.tv_djzd);
        tvTsbc = (TextView) view.findViewById(R.id.tv_tsbc);
        tvMore = (TextView) view.findViewById(R.id.tv_more);
        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) view.findViewById(R.id.tv_right_operate);
        tvSearchAddress = (TextView) view.findViewById(R.id.tv_search_address);
        rlTopTitle = (RelativeLayout) view.findViewById(R.id.rl_top_title);
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
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("党建地图");

        if (isLogin()) {
            accountBean = PreferenceUtil.getUserInfo(getActivity());
        }

        //如果用户信息不为空则请求数据
        weakReference = new WeakReference<Activity>(getActivity());
        gaodeMap.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = gaodeMap.getMap();
//            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
            aMap.setMapType(-2);// 矢量地图模式
            //设置不显示文字
            aMap.showMapText(false);
            setUpMap();
        }

//        setMapLocation();

        //绘制区域
        drawPolygon();

        // 设置当前地图显示为当前位置
        changeCamera(
                CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(accountBean.latitude), Double.valueOf(accountBean.longitude)), 25)
        );
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

//        aMap.addPolyline((new PolylineOptions())
//                .addAll(points)
//                .width(5)
//                .color(ContextCompat.getColor(getActivity(), R.color.color_facd8f)));

//        PlaneOption option = new PlaneOption();
//        option.setStrokeWidth(5);
//        option.setDottedLine(false);
//        option.setFillColor(ContextCompat.getColor(getActivity(), R.color.color_facd8f));
//        option.setStrokeColor(ContextCompat.getColor(getActivity(), R.color.color_facd8f));
//
//        PolylineOverlay overlay = new PolylineOverlay();
//        overlay.setOption(option);
//        overlay.setPoints(points);
//        mapView.addOverlay(overlay);
    }

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
        aMap.getUiSettings().setMyLocationButtonEnabled(true);

        //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);

        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {
        aMap.moveCamera(update);
    }

    private void setUpMap() {
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
                ToastHelper.showShort(marker.toString());
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
                VillageBaseInfoActivity.startActivity(getActivity(), true, accountBean.unitId);
                break;

            case R.id.tv_cjzz:
                VillageListActivity.startActivity(getActivity(), VillageListActivity.TYPE_CJZZ);
                break;

            case R.id.tv_djzd:
                VillageBaseInfoActivity.startActivity(getActivity(), false, accountBean.unitId);
                break;

            case R.id.tv_tsbc:
                VillageListActivity.startActivity(getActivity(), VillageListActivity.TYPE_TSBC);
                break;

            case R.id.tv_more:
                VillageListActivity.startActivity(getActivity(), VillageListActivity.TYPE_MORE);
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
        gaodeMap.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        gaodeMap.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gaodeMap.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁时，需要销毁定位client

        gaodeMap.onDestroy();
    }


}
