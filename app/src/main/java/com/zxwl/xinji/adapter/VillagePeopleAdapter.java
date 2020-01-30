package com.zxwl.xinji.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.DzzcyBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.VillageListActivity;
import com.zxwl.xinji.adapter.item.StudyListItem;
import com.zxwl.xinji.bean.StudyHeadBean;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/12/3 15:00
 */
public class VillagePeopleAdapter extends BaseSectionMultiItemQuickAdapter<StudyListItem, BaseViewHolder> {

    public static final int TYPE_HEAD = 1;
    public static final int TYPE_CONTENT = 2;

    public VillagePeopleAdapter(int sectionHeadResId, List<StudyListItem> data) {
        super(sectionHeadResId, data);
        addItemType(TYPE_HEAD, R.layout.item_village_people_head);
        addItemType(TYPE_CONTENT, R.layout.item_village_people);
    }

    @Override
    protected void convert(BaseViewHolder helper, StudyListItem listItem) {
        switch (helper.getItemViewType()) {
            case TYPE_HEAD:
                StudyHeadBean headBean = (StudyHeadBean) listItem.newsBean;
                TextView tvTitle = helper.getView(R.id.tv_title);
                tvTitle.setText(headBean.title);
                tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(headBean.resId, 0, 0, 0);
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
        CurrencyBean item = listItem.newsBean;

        ConstraintLayout clContent = helper.getView(R.id.cl_content);

        if (item instanceof DzzcyBean) {
            DzzcyBean dzzcyBean = (DzzcyBean) item;

            if (dzzcyBean.isLast) {
                clContent.setBackgroundResource(R.drawable.shape_bg_white_bottom_radius_10);
            } else {
                clContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }

            helper.setText(R.id.tv_name, dzzcyBean.name);
            helper.setText(R.id.tv_sex, dzzcyBean.sexValue);
            helper.setText(R.id.tv_lxfs, dzzcyBean.telephone);

            switch (dzzcyBean.itemTyep) {
                //村党组织成员
                case VillageListActivity.TYPE_DZZCY:
                    //村委会成员
                case VillageListActivity.TYPE_CWHCY:
                    helper.setText(R.id.tv_dzzzw_lable, "村党组织职务");
                    helper.setText(R.id.tv_cwhzw_lable, "村委会职务");

                    helper.setText(R.id.tv_dzzzw, dzzcyBean.orgPositionValue);
                    helper.setText(R.id.tv_cwhzw, dzzcyBean.villagePositionValue);
                    break;

                //村监会成员
                case VillageListActivity.TYPE_CJHCY:
                    //合作组织成员
                case VillageListActivity.TYPE_HZZZCY:
                    helper.setText(R.id.tv_dzzzw_lable, "职务");
                    helper.setText(R.id.tv_cwhzw_lable, "政治面貌");

                    helper.setText(R.id.tv_dzzzw, dzzcyBean.positionValue);
                    helper.setText(R.id.tv_cwhzw, dzzcyBean.politicalStatusValue);
                    break;
            }
        }
    }

    @Override
    protected void convertHead(BaseViewHolder helper, StudyListItem item) {

    }

}
