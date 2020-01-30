package com.zxwl.network.bean.response;

import java.util.Objects;

/**
 * author：pc-20171125
 * data:2019/8/28 15:10
 * 经纬度的bean
 */
public class LonlatBean {

    /**
     * checkOnline : 0
     * departmentId : 10
     * departmentName  : 中里厢乡
     * id : 4
     * latitude : 38.086832
     * longitude : 115.341568
     * parentId : 1
     */
    public int id;
    public int checkOnline;
    public int departmentId;
    public String departmentName;
    public String siteUri;
    public double latitude;
    public double longitude;
    public int parentId;
    public int areaType ;//1：市级别，2：乡镇，3：街村

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LonlatBean that = (LonlatBean) o;
        return id == that.id &&
                departmentId == that.departmentId &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                parentId == that.parentId &&
                Objects.equals(departmentName, that.departmentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, departmentId, departmentName, latitude, longitude, parentId);
    }
}
