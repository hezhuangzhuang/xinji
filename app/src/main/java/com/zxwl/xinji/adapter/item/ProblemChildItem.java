package com.zxwl.xinji.adapter.item;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.zxwl.xinji.adapter.ProblemAdapter;

/**
 * 子节点
 */
public class ProblemChildItem extends AbstractExpandableItem<String> implements MultiItemEntity {
    public String confRoomBean;

    public ProblemChildItem(String confRoomBean) {
        this.confRoomBean = confRoomBean;
    }

    @Override
    public int getItemType() {
        return ProblemAdapter.TYPE_CHILD;
    }

    @Override
    public int getLevel() {
        return 1;
    }
}
