package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.widget.CircleImageView;
import com.zxwl.network.bean.response.CommentBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.util.List;

/**
 * 评论的适配器
 */
public class CommentAdapter extends BaseQuickAdapter<CommentBean, BaseViewHolder> {
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CommentAdapter(int layoutResId, @Nullable List<CommentBean> data) {
        super(layoutResId, data);
    }

    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBean item) {
        helper.setText(R.id.tv_name, item.createName);
        helper.setText(R.id.tv_content, item.context);
        //获取当前的userid
        int userId = -1;
        if (isLogin()) {
            userId = Integer.valueOf(PreferenceUtil.getUserInfo(mContext).id);
        }

        TextView tvDel = helper.getView(R.id.tv_del);

        if (item.creator == userId) {
            tvDel.setVisibility(View.VISIBLE);
        } else {
            tvDel.setVisibility(View.GONE);
        }

        helper.setText(R.id.tv_time, DateUtil.getDescriptionTimeFromTimestamp(item.createDate));

        CircleImageView imageView = helper.getView(R.id.iv_head);

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_minel_head).error(R.mipmap.ic_minel_head))
                .load(item.url)
                .into(imageView);


        helper.addOnClickListener(R.id.tv_del);
    }
}
