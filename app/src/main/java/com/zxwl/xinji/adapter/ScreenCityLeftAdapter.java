package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/10/17 10:35
 */
public class ScreenCityLeftAdapter extends BaseQuickAdapter<DepartmentBean, BaseViewHolder> {
    private int selectIndex = -1;

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public ScreenCityLeftAdapter(int layoutResId, @Nullable List<DepartmentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DepartmentBean item) {
        helper.setText(R.id.tv_content, item.departmentName);

        TextView tvContent = helper.getView(R.id.tv_content);

        if (selectIndex == helper.getPosition()) {
            helper.setBackgroundColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.white));
            helper.setTextColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.color_333));
            TextPaint paint = tvContent.getPaint();
            paint.setFakeBoldText(true);
        } else {
            helper.setBackgroundColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.color_eee));
            helper.setTextColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.color_63646b));
            TextPaint paint = tvContent.getPaint();
            paint.setFakeBoldText(false);
        }
    }
}
