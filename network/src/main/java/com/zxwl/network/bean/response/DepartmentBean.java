package com.zxwl.network.bean.response;

import java.io.Serializable;
import java.util.Objects;

/**
 * author：pc-20171125
 * data:2019/8/15 09:57
 */
public class DepartmentBean implements Serializable {
    /**
     * id : 3
     * parentId : 2
     * selected : 0
     * areaType : 3
     * childrenSize : 0
     * isParent : false
     * orderid : 1
     * departmentName : 辖小辛庄
     */
    public int id;

    //父级id
    public int parentId;

    public String selected;

    //组织单位类型
    //1：市级
    //2：乡镇
    //3：街村
    public int areaType;

    //下级数据个数
    public int childrenSize;

    //0:下级无数据
    //1:下级有数据
    public int isParent;

    public String orderid;

    //departmentName
    public String departmentName;

    //名称首字母
    public String firstChar;

    public String createTime;
    public int orderId;
    public int state;
    public int treeParentId;

    public String terUri;
    public String siteId;

    public boolean isCheck = false;//是否选中

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentBean that = (DepartmentBean) o;
        return id == that.id &&
                parentId == that.parentId &&
                areaType == that.areaType &&
                Objects.equals(departmentName, that.departmentName) &&
                Objects.equals(terUri, that.terUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, areaType, departmentName, terUri);
    }
}
