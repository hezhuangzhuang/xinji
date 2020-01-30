package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.StatisticsBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.fragment.StatisticsFragment;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/6 16:45
 * 数据统计
 * 三会一课，两学一做，组织生活
 */
public class StatisticsConfAdapter extends BaseQuickAdapter<StatisticsBean, BaseViewHolder> {
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public StatisticsConfAdapter(int layoutResId, @Nullable List<StatisticsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StatisticsBean item) {
        TextView tvContent = (TextView) helper.getView(R.id.tv_content);
        TextView tvName = (TextView) helper.getView(R.id.tv_name);
        TextView tvNumber = (TextView) helper.getView(R.id.tv_number);
        RoundCornerProgressBar progress = (RoundCornerProgressBar) helper.getView(R.id.progress);

        helper.setText(R.id.tv_rate, item.rate);

        progress.setMax(100);
        progress.setProgress(item.num);

        String unitName = "辛集市";

        if (!TextUtils.isEmpty(item.townidVal) && !TextUtils.isEmpty(item.villageidVal)) {
            unitName = item.townidVal + item.villageidVal;
        } else if (!TextUtils.isEmpty(item.townidVal)) {
            unitName = item.townidVal;
        }

        tvName.setText(unitName);
        tvNumber.setText(String.valueOf(item.num));
        if (StatisticsFragment.TYPE_SHYK.equals(type)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("党员大会" + item.type1 + "\t\t\t" + "党支部会" + item.type2 + "\t\t\t" + "党小组会" + item.type3 + "\t\t\t" + "党课" + item.type4);
        } else if (StatisticsFragment.TYPE_XXJY.equals(type)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("组织工作" + item.type1 + "\t\t\t" + "乡村动态" + item.type2 + "\t\t\t" + "先进典型" + item.type3 + "\t\t\t" + "他山之石" + item.type4);
//            tvContent.setText("党建资讯" + item.type1 + "\t\t\t" + "乡村动态" + item.type2 + "\t\t\t" + "典型经验" + item.type3 + "\t\t\t" + "他山之石" + item.type4);
        } else {
            tvContent.setVisibility(View.GONE);
        }
    }
}