package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/11/19 19:27
 */
public class VideoCallAdapter extends BaseQuickAdapter<DepartmentBean, BaseViewHolder> {
    /**
     * 模式
     */
    private int mode;

    public void setMode(int mode) {
        this.mode = mode;
    }

    public static final int MODE_SPHJ = 1;//呼叫模式
    public static final int MODE_SPHY = 2;//选择模式
    public static final int MODE_YXZ = 3;//删除模式

    public VideoCallAdapter(int layoutResId, @Nullable List<DepartmentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DepartmentBean item) {
        ConstraintLayout rlContent = (ConstraintLayout) helper.getView(R.id.rl_content);
        ImageView ivCall = (ImageView) helper.getView(R.id.iv_call);
        TextView tvName = (TextView) helper.getView(R.id.tv_name);
        ImageView ivDelete = (ImageView) helper.getView(R.id.iv_delete);
        ImageView ivSelect = (ImageView) helper.getView(R.id.iv_select);

        tvName.setText(item.departmentName);

        tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(1 == item.parentId ? R.mipmap.ic_countryside : R.mipmap.ic_street, 0, 0, 0);

        switch (mode) {
            case MODE_SPHJ:
                ivSelect.setVisibility(View.GONE);
                ivCall.setVisibility(View.VISIBLE);
                helper.addOnClickListener(R.id.rl_content, R.id.iv_call);
                break;

            case MODE_SPHY:
                if(item.isCheck){
                    ivSelect.setImageResource(R.mipmap.ic_select_person_true);
                }else {
                    ivSelect.setImageResource(R.mipmap.ic_select_person_false);
                }
                ivSelect.setVisibility(View.VISIBLE);
                helper.addOnClickListener(R.id.rl_content, R.id.iv_select);
                break;

            case MODE_YXZ:
                ivDelete.setVisibility(View.VISIBLE);
                helper.addOnClickListener( R.id.iv_delete);
                break;
        }
    }
}
