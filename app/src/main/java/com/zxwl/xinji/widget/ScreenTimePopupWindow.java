package com.zxwl.xinji.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.ScreenTimeAdapter;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * author：pc-20171125
 * data:2019/11/12 14:12
 * 时间筛选适配器
 */
public class ScreenTimePopupWindow extends BasePopupWindow {
    private RecyclerView rvList;
    private ScreenTimeAdapter adapter;
    private List<String> dataList;

    public ScreenTimePopupWindow(Context context,
                                 int width,
                                 int height,
                                 List<String> dataList) {
        super(context, width, height);

        this.dataList = dataList;

        findViews();
    }

    private void findViews() {
        rvList = findViewById(R.id.rv_list);
        adapter = new ScreenTimeAdapter(R.layout.item_screen_time, dataList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (null != screenClick) {
                    screenClick.onItemClick(position);
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(adapter);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.recycler_layout);
    }

    public interface onScreenClick {
        void onItemClick(int position);
    }

    private onScreenClick screenClick;

    public void setOnScreenClick(onScreenClick screenClick) {
        this.screenClick = screenClick;
    }
}
