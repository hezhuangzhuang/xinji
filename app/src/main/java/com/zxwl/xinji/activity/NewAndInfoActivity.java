package com.zxwl.xinji.activity;

import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.zxwl.commonlibrary.widget.NoScrollViewPager;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.MyPagerAdapter;
import com.zxwl.xinji.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class NewAndInfoActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private SlidingTabLayout tabLayout;
    private NoScrollViewPager vpContent;

    private List<String> mTitles;

    private List<Fragment> mFragments = new ArrayList<>();

    private int currentIndex = 0;//当前下标

    private LoginBean.AccountBean accountBean;

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tb_layout);
        vpContent = (NoScrollViewPager) findViewById(R.id.vp_content);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_and_info;
    }


    private void initTab() {
        for (int i = 0; i < mTitles.size(); i++) {
            if (i == 0) {
//                mFragments.add(NewsListFragment.newInstance(mTitles.get(i).id, true));
            } else {
//                mFragments.add(NewsListFragment.newInstance(mTitles.get(i).id, false));
            }
        }
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        vpContent.setAdapter(mAdapter);
        tabLayout.setViewPager(vpContent);

        vpContent.setCurrentItem(currentIndex);
        vpContent.setOffscreenPageLimit(mTitles.size());

        tabLayout.setCurrentTab(currentIndex);
        tabLayout.onPageSelected(currentIndex);
    }

}
