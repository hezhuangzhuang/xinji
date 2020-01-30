package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.DocumentBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/10/17 15:38
 * 组工文件适配器
 */
public class DocumentAdapter extends BaseQuickAdapter<DocumentBean, BaseViewHolder> {
    private String searchContent;

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public DocumentAdapter(int layoutResId, @Nullable List<DocumentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DocumentBean item) {
        String title = item.title;

        SpannableString spannableString = new SpannableString(title);

//        Spanned.SPAN_INCLUSIVE_EXCLUSIVE 从起始下标到终止下标，包括起始下标
//        Spanned.SPAN_INCLUSIVE_INCLUSIVE 从起始下标到终止下标，同时包括起始下标和终止下标
//        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE 从起始下标到终止下标，但都不包括起始下标和终止下标
//        Spanned.SPAN_EXCLUSIVE_INCLUSIVE 从起始下标到终止下标，包括终止下标
        if(TextUtils.isEmpty(searchContent)){
            helper.setText(R.id.tv_content,item.title);
        }else {
            int i = title.indexOf(searchContent);
            if (i > -1) {
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.color_E42417)), i, i + searchContent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                helper.setText(R.id.tv_content,spannableString);
            }else {
                helper.setText(R.id.tv_content,item.title);
            }
        }
    }
}
