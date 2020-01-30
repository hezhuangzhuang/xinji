package com.zxwl.xinji.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MapViewRender;
import com.tianditu.android.maps.Overlay;
import com.tianditu.android.maps.renderoption.DrawableOption;
import com.zxwl.xinji.R;

import javax.microedition.khronos.opengles.GL10;

/**
 * author：pc-20171125
 * data:2019/7/31 11:41
 */
public class MyOverlay extends Overlay {
    private Drawable mDrawable = null;
    private DrawableOption mOption = null;
    private GeoPoint mGeoPoint = null;

    public MyOverlay(Context context) {
        mDrawable =
                context.getResources().getDrawable(R.mipmap.ic_overlay);
        mOption = new DrawableOption();
        mOption.setAnchor(0.5f, 1.0f);
    }

    @Override
    public boolean onTap(GeoPoint point, MapView mapView) {
        mGeoPoint = point;
        return true;
    }

    @Override
    public void draw(GL10 gl, MapView mapView, boolean shadow) {
        if (shadow){
            return;
        }
        MapViewRender render = mapView.getMapViewRender();
        render.drawDrawable(gl, mOption, mDrawable, mGeoPoint);
    }
}