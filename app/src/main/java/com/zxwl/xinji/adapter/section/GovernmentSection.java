package com.zxwl.xinji.adapter.section;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.zxwl.xinji.bean.GovernmentBean;

/**
 * author：pc-20171125
 * data:2019/7/30 16:52
 * @author pc-20171125
 */
public class GovernmentSection extends SectionEntity<GovernmentBean> {
    private GovernmentBean governmentBean;

    public GovernmentSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public GovernmentSection(GovernmentBean governmentBean) {
        super(governmentBean);
        this.governmentBean = governmentBean;
    }
}
