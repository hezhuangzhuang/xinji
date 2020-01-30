package com.zxwl.network.bean.response;

import android.support.annotation.NonNull;

/**
 * author：pc-20171125
 * data:2019/10/23 17:10
 */
public class VotePeopleDetailsBean implements Comparable<VotePeopleDetailsBean> {
    /**
     * id : 50
     * introduction : 1
     * name : 1
     * pic1 : http://192.168.20.249:8080/xjdj/20191023152305344_2.jpeg
     * voteId : 122
     */
    public int id;
    public String introduction;
    public String name;
    public String pic1;
    public String pic2;
    public String pic3;
    public String pic4;
    public String pic5;
    public int voteId;
    public int order;

    public int num;//票数
    
    public boolean isVote = false;//是否投过票

    public int disparity;//距上一名的票数
    public int ranking;//名次

    @Override
    public int compareTo(@NonNull VotePeopleDetailsBean votePeopleDetailsBean) {
        return votePeopleDetailsBean.num - num;
    }

    @Override
    public String toString() {
        return "VotePeopleDetailsBean{" +
                "name='" + name + '\'' +
                ", ranking=" + ranking +
                ", disparity=" + disparity +
                '}';
    }
}
