package com.zxwl.xinji.widget;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.zxwl.xinji.R;
import com.zxwl.xinji.utils.map.AMapUtil;

import razerdp.basepopup.BasePopupWindow;

/**
 * author：pc-20171125
 * data:2019/12/6 16:53
 */
public class MapAddressDialog extends BasePopupWindow {
    private PoiItem poiItem;

    private TextView tvTitle;
    private TextView tvSnippet;
    private FrameLayout flNavi;
    private FrameLayout flLine;

    //两点间的距离
    private float distance;

    public void setDistance(float distance) {
        this.distance = distance;
        tvSnippet.setText("距您" + AMapUtil.getFriendlyLength((int) distance) + " | " + poiItem.getSnippet());
    }

    public MapAddressDialog(Context context, int width, int height, PoiItem poiItem) {
        super(context, width, height);
        this.poiItem = poiItem;

        initViews();
    }

    private void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSnippet = (TextView) findViewById(R.id.tv_snippet);
        flNavi = (FrameLayout) findViewById(R.id.fl_navi);
        flLine = (FrameLayout) findViewById(R.id.fl_line);

        tvTitle.setText(poiItem.getTitle());
//        tvSnippet.setText(poiItem.getSnippet());

        flNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != addressClickListener) {
                    addressClickListener.onNavi(poiItem);
                }
            }
        });

        flLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != addressClickListener) {
                    addressClickListener.onLine(poiItem);
                }
            }
        });
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_map_address_bottom);
    }

    public void setPoiItem(PoiItem poiItem) {
        this.poiItem = poiItem;
        tvTitle.setText(poiItem.getTitle());
    }

    public interface onAddressClickListener {
        void onNavi(PoiItem poiItem);

        void onLine(PoiItem poiItem);
    }

    private onAddressClickListener addressClickListener;

    public void setAddressClickListener(onAddressClickListener addressClickListener) {
        this.addressClickListener = addressClickListener;
    }
}
