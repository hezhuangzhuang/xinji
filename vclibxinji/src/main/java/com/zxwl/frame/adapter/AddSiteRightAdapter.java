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
import com.zxwl.network.bean.response.DepartmentBean;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/1/3 17:43
 */
public class AddSiteRightAdapter extends RecyclerView.Adapter<AddSiteRightAdapter.GroupUserHolder> {
    private Context context;
    private List<DepartmentBean> list;

    public List<DepartmentBean> getDatas() {
        return list;
    }

    public AddSiteRightAdapter(Context context, List<DepartmentBean> list) {
        this.context = context;
        this.list = list;
    }

    public void replaceData(List<DepartmentBean> data) {
        // 不是同一个引用才清空列表
        if (data != list) {
            list.clear();
            list.addAll(data);
        }
        notifyDataSetChanged();
    }

    private onRecyclerClick recyclerClick;

    public void setRecyclerClick(onRecyclerClick recyclerClick) {
        this.recyclerClick = recyclerClick;
    }

    @Override
    public GroupUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_site_left, null);
        return new GroupUserHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupUserHolder holder, int position) {
        DepartmentBean userBean = list.get(position);
        holder.tvName.setText(userBean.departmentName);
        holder.tvName.setTextColor(ContextCompat.getColor(context,userBean.isCheck?R.color.color_ccc:R.color.color_999));
        holder.ivCheck.setImageResource(userBean.isCheck ? R.mipmap.ic_no_login_select : R.mipmap.ic_no_login_un_select);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != recyclerClick) {
                    recyclerClick.onCheck(position);
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
