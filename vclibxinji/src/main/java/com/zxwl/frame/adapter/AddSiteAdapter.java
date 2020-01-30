package com.zxwl.frame.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxwl.frame.R;
import com.zxwl.frame.bean.respone.GroupUser;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/1/3 17:43
 * 会控的适配器
 */
public class AddSiteAdapter extends RecyclerView.Adapter<AddSiteAdapter.GroupUserHolder> {
    private Context context;
    private List<GroupUser.DataBean> list;

    public List<GroupUser.DataBean> getDatas() {
        return list;
    }

    public AddSiteAdapter(Context context, List<GroupUser.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    private onRecyclerClick recyclerClick;

    public void setRecyclerClick(onRecyclerClick recyclerClick) {
        this.recyclerClick = recyclerClick;
    }

    @Override
    public GroupUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, null);
        return new GroupUserHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupUserHolder holder, int position) {
        GroupUser.DataBean userBean = list.get(position);
        holder.tvName.setText(userBean.name);
        holder.ivCheck.setImageResource(userBean.isCheck ? R.mipmap.ic_no_login_select : R.mipmap.ic_no_login_un_select);
        holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.white));
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != recyclerClick) {
                    recyclerClick.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == list ? 0 : list.size();
    }

    class GroupUserHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView tvName;
        public ImageView ivCheck;

        public GroupUserHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivCheck = itemView.findViewById(R.id.iv_check);
            rootView = itemView.findViewById(R.id.rl_content);
        }
    }
}
