package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.SearchBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/6/18 15:49
 */
public class SearchAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {

    public String searchContent;

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public SearchAdapter(int layoutResId, @Nullable List<SearchBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        helper.addOnClickListener(R.id.rl_content);
        TextView tvContentNumber = helper.getView(R.id.tv_content_number);
        TextView tvWebsite = helper.getView(R.id.tv_website);
        TextView tvTime = helper.getView(R.id.tv_time);

        tvContentNumber.setVisibility(View.GONE);
        tvWebsite.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);

        TextView tvTitle = helper.getView(R.id.tv_title);

        String title = item.title;

        SpannableString spannableString = new SpannableString(title);

//        Spanned.SPAN_INCLUSIVE_EXCLUSIVE 从起始下标到终止下标，包括起始下标
//        Spanned.SPAN_INCLUSIVE_INCLUSIVE 从起始下标到终止下标，同时包括起始下标和终止下标
//        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE 从起始下标到终止下标，但都不包括起始下标和终止下标
//        Spanned.SPAN_EXCLUSIVE_INCLUSIVE 从起始下标到终止下标，包括终止下标
        int i = title.indexOf(searchContent);
        if (i > -1) {
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.color_E42417)), i, i + searchContent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        tvTitle.setText(spannableString);
    }
}
