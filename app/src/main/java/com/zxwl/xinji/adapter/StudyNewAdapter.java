package com.zxwl.xinji.adapter;

import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.bean.response.ZtjyBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.item.StudyListItem;
import com.zxwl.xinji.bean.StudyHeadBean;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/7 14:08
 * 学习新的适配器
 */
public class StudyNewAdapter extends BaseSectionMultiItemQuickAdapter<StudyListItem, BaseViewHolder> {

    public static final int TYPE_HEAD = 1;
    public static final int TYPE_CONTENT = 2;

    public StudyNewAdapter(int sectionHeadResId, List<StudyListItem> data) {
        super(sectionHeadResId, data);
        addItemType(TYPE_HEAD, R.layout.item_study_head);
        addItemType(TYPE_CONTENT, R.layout.item_study_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, StudyListItem listItem) {
        switch (helper.getItemViewType()) {
            case TYPE_HEAD:
                StudyHeadBean headBean = (StudyHeadBean) listItem.newsBean;
                helper.setImageResource(R.id.iv_lable, headBean.resId);
                break;

            case TYPE_CONTENT:
                setContent(helper, listItem);
                break;
        }
    }

    /**
     * 设置内容
     *
     * @param helper
     * @param listItem
     */
    private void setContent(BaseViewHolder helper, StudyListItem listItem) {
        ImageView imageView = helper.getView(R.id.iv_img);

        CurrencyBean item = listItem.newsBean;

        if (item instanceof NewsBean) {
            NewsBean newsBean = (NewsBean) item;
            Glide.with(mContext)
                    .asBitmap()
                    .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
                    .load(newsBean.pic1)
                    .into(imageView);

            imageView.setBackgroundResource(R.drawable.shape_img_bg_3dp);

            helper.setText(R.id.tv_title, newsBean.title);
            helper.setText(R.id.tv_content, newsBean.announcer);
            helper.setText(R.id.tv_time, DateUtil.longToString(newsBean.createDate, DateUtil.FORMAT_DATE));
        } else {
            ZtjyBean ztjyBean = (ZtjyBean) item;
            switch (ztjyBean.educTypeVal){
               //"党建资讯"
                case RefreshRecyclerFragment.TYPE_DJZX:
                    imageView.setImageResource(R.mipmap.ic_work_djzx);
                    break;

                //"乡村动态"
                case RefreshRecyclerFragment.TYPE_XCDT:
                    imageView.setImageResource(R.mipmap.ic_work_xcdt);
                    break;

                //"典型经验"
                case "典型经验":
                case RefreshRecyclerFragment.TYPE_DXJY:
                    imageView.setImageResource(R.mipmap.ic_work_dxjy);
                    break;

                //"他山之石"
                case RefreshRecyclerFragment.TYPE_TSZS:
                    imageView.setImageResource(R.mipmap.ic_work_tszs);
                    break;
            }
//            Glide.with(mContext)
//                    .asBitmap()
//                    .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
//                    .load(ztjyBean.pic1)
//                    .into(imageView);

            helper.setText(R.id.tv_title, ztjyBean.theme);

            String unitName = "辛集市";
            if (!TextUtils.isEmpty(ztjyBean.villagename)) {
                unitName = ztjyBean.villagename;
            } else if (!TextUtils.isEmpty(ztjyBean.vtownsname)) {
                unitName = ztjyBean.vtownsname;
            }

            String createName = TextUtils.isEmpty(ztjyBean.creatorName) ? "" : ztjyBean.creatorName;

            helper.setText(R.id.tv_content, createName);
            helper.setText(R.id.tv_time, DateUtil.longToString(ztjyBean.eduDate, DateUtil.FORMAT_DATE));
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

    @Override
    protected void convertHead(BaseViewHolder helper, StudyListItem item) {

    }

}
