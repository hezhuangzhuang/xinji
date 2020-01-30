package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.bean.response.ZtjyBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/7 14:08
 * 学习的适配器
 */
public class StudyAdapter extends BaseQuickAdapter<CurrencyBean, BaseViewHolder> {
    public StudyAdapter(int layoutResId, @Nullable List<CurrencyBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CurrencyBean item) {
        ImageView imageView = helper.getView(R.id.iv_img);

        if (item instanceof NewsBean) {
            NewsBean newsBean = (NewsBean) item;
            Glide.with(mContext)
                    .asBitmap()
                    .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
                    .load(newsBean.pic1)
                    .into(imageView);

            helper.setText(R.id.tv_title, newsBean.title);
            helper.setText(R.id.tv_content, newsBean.announcer);
            helper.setText(R.id.tv_time, DateUtil.longToString(newsBean.createDate, DateUtil.FORMAT_DATE));
        } else {
            ZtjyBean ztjyBean = (ZtjyBean) item;
            Glide.with(mContext)
                    .asBitmap()
                    .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
                    .load(ztjyBean.pic1)
                    .into(imageView);

            helper.setText(R.id.tv_title, ztjyBean.theme);

            String unitName = "辛集市";
            if (!TextUtils.isEmpty(ztjyBean.villagename)) {
                unitName = ztjyBean.villagename;
            } else if (!TextUtils.isEmpty(ztjyBean.vtownsname)) {
                unitName = ztjyBean.vtownsname;
            }

            String createName = TextUtils.isEmpty(ztjyBean.creatorName) ? "" : ztjyBean.creatorName;

            helper.setText(R.id.tv_content, createName);
            helper.setText(R.id.tv_time, DateUtil.longToString(ztjyBean.createDate, DateUtil.FORMAT_DATE));
        }

        TextView tvTime = helper.getView(R.id.tv_time);
        ViewTreeObserver vto = tvTime.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tvTime.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Layout layout = tvTime.getLayout();
                if (layout != null) {
                    int lines = layout.getLineCount();
                    if (layout.getEllipsisCount(lines - 1) > 0) { //有省略
                        tvTime.setVisibility(View.GONE);
                    } else {
                        tvTime.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
