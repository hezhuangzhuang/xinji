package com.zxwl.network.bean.response;

import android.support.annotation.NonNull;

/**
 * author：pc-20171125
 * data:2019/8/15 16:20
 * 投票候选人
 */
public class VotePeopleBean implements Comparable<VotePeopleBean> {


    /**
     * rank : 1
     * count : 10
     * voteid : 59
     * name : 李四
     * pic : http://192.168.20.249:8080/xjdj/picFolder/news/20190808093730485_2.jpeg
     * partyid : 8
     */
    public int rank;//排名
    public int voteid;
    public int count;
    public String name;
    public String pic1;
    public int partyid;
    public String partyname;

    @Override
    public int compareTo(@NonNull VotePeopleBean voteBean) {
        return rank - voteBean.rank;
    }
}
