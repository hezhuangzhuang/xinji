package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.DzzcyBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.VillageListActivity;

import java.util.List;

/**
 * 党组织成员
 */
public class CdzzcyAdapter extends BaseQuickAdapter<DzzcyBean, BaseViewHolder> {

    public CdzzcyAdapter(int layoutResId, @Nullable List<DzzcyBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DzzcyBean item) {
        ConstraintLayout clContent = helper.getView(R.id.cl_content);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) clContent.getLayoutParams();
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        clContent.setLayoutParams(layoutParams);

        helper.setText(R.id.tv_name, item.name);
        helper.setText(R.id.tv_sex, item.sexValue);
        helper.setText(R.id.tv_lxfs, item.telephone);

        switch (item.itemTyep) {
            //村党组织成员
            case VillageListActivity.TYPE_DZZCY:
                //村委会成员
            case VillageListActivity.TYPE_CWHCY:
                helper.setText(R.id.tv_dzzzw_lable, "村党组织职务");
                helper.setText(R.id.tv_cwhzw_lable, "村委会职务");

                helper.setText(R.id.tv_dzzzw, item.orgPositionValue);
                helper.setText(R.id.tv_cwhzw, item.villagePositionValue);
                break;

            //村监会成员
            case VillageListActivity.TYPE_CJHCY:
                //合作组织成员
            case VillageListActivity.TYPE_HZZZCY:
                helper.setText(R.id.tv_dzzzw_lable, "职务");
                helper.setText(R.id.tv_cwhzw_lable, "政治面貌");

                helper.setText(R.id.tv_dzzzw, item.positionValue);
                helper.setText(R.id.tv_cwhzw, item.politicalStatusValue);
                break;
        }
    }

}
