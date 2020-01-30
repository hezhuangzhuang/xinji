package com.zxwl.xinji.adapter.item;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.zxwl.xinji.adapter.ProblemAdapter;

/**
 * 父节点
 */
public class ProblemParentItem extends AbstractExpandableItem<ProblemChildItem> implements MultiItemEntity {
    public String conferenceHall;

    //是否展开
    public boolean isExpand = false;

    public ProblemParentItem(String conferenceHall) {
        this.conferenceHall = conferenceHall;
    }

    @Override
    public int getItemType() {
        return ProblemAdapter.TYPE_PERSON;
    }

    @Override
    public int getLevel() {
        return ProblemAdapter.TYPE_PERSON;
    }
}
