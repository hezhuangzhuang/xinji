package com.zxwl.xinji.adapter;

import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.item.StudyListItem;
import com.zxwl.xinji.bean.StudyHeadBean;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/12/3 15:00
 */
public class OrganizingLifeAdapter extends BaseSectionMultiItemQuickAdapter<StudyListItem, BaseViewHolder> {

    public static final int TYPE_HEAD = 1;
    public static final int TYPE_CONTENT = 2;

    public OrganizingLifeAdapter(int sectionHeadResId, List<StudyListItem> data) {
        super(sectionHeadResId, data);
        addItemType(TYPE_HEAD, R.layout.item_study_head);
        addItemType(TYPE_CONTENT, R.layout.item_study_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, StudyListItem listItem) {
        switch (helper.getItemViewType()) {
            case TYPE_HEAD:
                StudyHeadBean headBean = (StudyHeadBean) listItem.newsBean;
                helper.setText(R.id.tv_title, headBean.title);
                helper.setVisible(R.id.tv_title,true);
                helper.setVisible(R.id.iv_lable, false);
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

        if (item instanceof ThemePartyBean) {
            ThemePartyBean themePartyBean = (ThemePartyBean) item;
//            Glide.with(mContext)
//                    .asBitmap()
//                    .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
//                    .load(themePartyBean.pic1)
//                    .into(imageView);

            helper.setText(R.id.tv_title, themePartyBean.title);
            helper.setText(R.id.tv_time, DateUtil.longToString(themePartyBean.activityDate, DateUtil.FORMAT_DATE));

            String createName = TextUtils.isEmpty(themePartyBean.creatorName) ? "admin" : themePartyBean.creatorName;
            helper.setText(R.id.tv_content, createName);

            switch (themePartyBean.itemTyep) {
                //"主题党日"
                case RefreshRecyclerFragment.TYPE_ZTDR:
                    imageView.setImageResource(R.mipmap.ic_left_ztdr);
                    break;

                //"三会一课"
                case RefreshRecyclerFragment.TYPE_SHYK:
                    imageView.setImageResource(R.mipmap.ic_left_shyk);
                    break;

                //"民主评议"
                case RefreshRecyclerFragment.TYPE_MZPY:
                    imageView.setImageResource(R.mipmap.ic_left_mzpydy);
                    break;

                //"组织生活会"
                case RefreshRecyclerFragment.TYPE_ZZSHH:
                    imageView.setImageResource(R.mipmap.ic_left_zzshh);
                    break;

                //"其他"
                case RefreshRecyclerFragment.TYPE_MORE:
                    imageView.setImageResource(R.mipmap.ic_left_more);
                    break;
            }
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
