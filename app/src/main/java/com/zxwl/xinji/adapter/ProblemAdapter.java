package com.zxwl.xinji.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.item.ProblemChildItem;
import com.zxwl.xinji.adapter.item.ProblemParentItem;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/7/31 16:01
 * 问题的适配器
 */
public class ProblemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_PERSON = 0;//父节点

    public static final int TYPE_CHILD = 1;//子节点

    public ProblemAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_PERSON, R.layout.item_problem_parent);
        addItemType(TYPE_CHILD, R.layout.item_problem_child);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (item.getItemType()) {
            //父节点
            case TYPE_PERSON:
                ProblemParentItem parentItem = (ProblemParentItem) item;
                String conferenceHall = parentItem.conferenceHall;

                ImageView arrow = helper.getView(R.id.list_item_genre_arrow);
                TextView tvPersonName = helper.getView(R.id.list_item_genre_name);

                helper.setText(R.id.list_item_genre_name, conferenceHall);

                helper.itemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pos = helper.getAdapterPosition();
                                int subItemCount = -1;
                                //展开
                                if (parentItem.isExpanded()) {
                                    //进行收起操作
                                    subItemCount = collapse(pos, true, true);
                                } else {
                                    //进行展开操作
                                    subItemCount = expand(pos, true, true);
                                }
                            }
                        }
                );
                break;

            //子节点
            case TYPE_CHILD:
                ProblemChildItem childItem = (ProblemChildItem) item;
                String confRoomBean = childItem.confRoomBean;
                helper.setText(R.id.tv_child_name, confRoomBean);
                break;
        }
    }

    private void animateExpand(ImageView arrow) {
//        RotateAnimation rotate = new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(3000);
//        rotate.setFillAfter(true);
//        arrow.setAnimation(rotate);

        arrow.setRotation(360);
    }

    private void animateCollapse(ImageView arrow) {
//        RotateAnimation rotate = new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(3000);
//        rotate.setFillAfter(true);
//        arrow.setAnimation(rotate);

        arrow.setRotation(180);
    }
}
