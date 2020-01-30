package com.zxwl.xinji.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.item.HistoryListItem;
import com.zxwl.xinji.adapter.item.MultipleItem;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/5/8 16:27
 * 历史适配器
 */
public class HistoryAdapter extends BaseSectionMultiItemQuickAdapter<HistoryListItem, BaseViewHolder> {

    private boolean isEdit = false;//是否处于编辑状态

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    public HistoryAdapter(int sectionHeadResId, List<HistoryListItem> data) {
        super(sectionHeadResId, data);

        addItemType(MultipleItem.TEXT, R.layout.item_history);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, HistoryListItem item) {
        helper.setText(R.id.tv_time, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, HistoryListItem item) {
        helper.addOnClickListener(R.id.tv_content);

        switch (helper.getItemViewType()) {
            case MultipleItem.TEXT:
                TextView tvContent = helper.getView(R.id.tv_content);
                tvContent.setText(item.historyBean.title);
                //处于编辑状态
                if (isEdit) {
                    tvContent.setVisibility(View.VISIBLE);
                    if (item.historyBean.isSelect) {
                        tvContent.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_collection_true, 0, 0, 0);
                    } else {
                        tvContent.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_collection_false, 0, 0, 0);
                    }
                } else {
                    tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                break;

            default:
                break;
        }
    }

}
