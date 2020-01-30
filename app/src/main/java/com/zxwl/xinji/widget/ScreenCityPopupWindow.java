package com.zxwl.xinji.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.ScreenCityLeftAdapter;
import com.zxwl.xinji.adapter.ScreenCityRightAdapter;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;


/**
 * author：pc-20171125
 * data:2019/10/17 10:12
 * 筛选城市
 */
public class ScreenCityPopupWindow extends BasePopupWindow {
    private RecyclerView rvLeft;
    private RecyclerView rvRight;

    private ScreenCityLeftAdapter leftAdapter;
    private ScreenCityRightAdapter rightAdapter;

    private List<DepartmentBean> leftData;
    private List<DepartmentBean> rightData;

    private FrameLayout flCancle;
    private TextView tvCancle;

    public ScreenCityPopupWindow(Context context,
                                 int width,
                                 int height,
                                 List<DepartmentBean> leftData,
                                 List<DepartmentBean> rightData) {
        super(context, width, height);

        this.leftData = leftData;
        this.rightData = rightData;

        findViews();
    }

    public ScreenCityPopupWindow(Context context,
                                 int width,
                                 int height,
                                 List<DepartmentBean> leftData,
                                 List<DepartmentBean> rightData,
                                 boolean isShowCancle) {
        this(context, width, height, leftData, rightData);

        flCancle.setVisibility(View.VISIBLE);
    }

    public void setLeftNewData(List<DepartmentBean> newLeftData) {
        leftAdapter.setNewData(newLeftData);
    }

    public void setRightNewData(List<DepartmentBean> newRightData) {
        rightAdapter.setNewData(newRightData);
        rvRight.smoothScrollToPosition(0);
    }

    public void setLeftSelectIndex(int position) {
        leftAdapter.setSelectIndex(position);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_city_select);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    //左边列表上次选中的下标
    private int leftLastSelectIndex = -1;


    /**
     * 创建view
     *
     * @return
     */
    private void findViews() {
        rvLeft = (RecyclerView) findViewById(R.id.rv_left);
        rvRight = (RecyclerView) findViewById(R.id.rv_right);
        flCancle = (FrameLayout) findViewById(R.id.fl_cancle);
        tvCancle = (TextView) findViewById(R.id.tv_cancle);

        leftAdapter = new ScreenCityLeftAdapter(R.layout.item_city_select_left, leftData);
        rightAdapter = new ScreenCityRightAdapter(R.layout.item_city_select_right, rightData);

        leftAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                leftAdapter.setSelectIndex(position);
                rightAdapter.setSelectIndex(-1);

                if (null != screenClick) {
                    DepartmentBean item = leftAdapter.getItem(position);
                    screenClick.onLeftClick(item.id, item.departmentName);
                }
            }
        });

        rightAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                rightAdapter.setSelectIndex(position);

                if (null != screenClick) {
                    DepartmentBean item = rightAdapter.getItem(position);
                    screenClick.onRightClick(item.id, item.departmentName, item.siteId);
                }
            }
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rvLeft.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRight.setLayoutManager(new LinearLayoutManager(getContext()));

        rvLeft.setAdapter(leftAdapter);
        rvRight.setAdapter(rightAdapter);
    }

    public interface onScreenClick {
        void onLeftClick(int cityId, String departmentName);

        void onRightClick(int cityId, String departmentName, String terUri);
    }

    private onScreenClick screenClick;

    public void setOnScreenClick(ScreenCityPopupWindow.onScreenClick screenClick) {
        this.screenClick = screenClick;
    }
}
