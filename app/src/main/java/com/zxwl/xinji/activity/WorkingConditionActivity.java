package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.widget.CustomViewPager;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.MyPagerAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作动态
 */
public class WorkingConditionActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private ImageView ivRightOperate;
    private TextView tvRightOperate;
    private TextView tvTopTitle;
    private SlidingTabLayout tabLayout;
    private CustomViewPager vpContent;

    private List<String> mTitles;

    private List<Fragment> mFragments = new ArrayList<>();

    private int currentIndex;

    public static final String TITLE = "TITLE";
    public static final String TYPE_GZDT = "工作动态";
    public static final String TYPE_WXY = "微心愿";

    private String title;

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, WorkingConditionActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        ivRightOperate = findViewById(R.id.iv_right_operate);
        tvRightOperate = findViewById(R.id.tv_right_operate);

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tb_layout);
        vpContent = (CustomViewPager) findViewById(R.id.vp_content);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);

        //设置可以滑动
        vpContent.setScroll(true);

        tvTopTitle.setText(title);

        tvRightOperate.setText("添加");
        tvRightOperate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.ic_wxy_add, 0);

        if (TYPE_WXY.equals(title)) {
            tvRightOperate.setVisibility(View.VISIBLE);
        } else {
            if (isLogin()) {
                accountBean = PreferenceUtil.getUserInfo(this);
                if (1 == accountBean.checkAdmin && 1 != accountBean.level) {
                    tvRightOperate.setVisibility(View.VISIBLE);
                } else {
                    tvRightOperate.setVisibility(View.GONE);
                }
            } else {
                tvRightOperate.setVisibility(View.GONE);
            }
        }
        initTab();
    }

    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRightOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TYPE_GZDT.equals(title)) {
                    ReleaseConfActivity.startActivity(WorkingConditionActivity.this, ReleaseConfActivity.TYPE_GZDT);
                } else {
                    ReleaseWxyActivity.startActivity(WorkingConditionActivity.this);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_organization_documents;
    }

    private void initTab() {
        mTitles = new ArrayList<>();
        if (TYPE_GZDT.equals(title)) {
            mTitles.add(RefreshRecyclerFragment.TYPE_DJZX);
            mTitles.add(RefreshRecyclerFragment.TYPE_XCDT);
            mTitles.add(RefreshRecyclerFragment.TYPE_DXJY);
            mTitles.add(RefreshRecyclerFragment.TYPE_TSZS);

            for (int i = 0; i < mTitles.size(); i++) {
                mFragments.add(RefreshRecyclerFragment.newInstance(mTitles.get(i), 0));
            }
        } else {
            mTitles.add("未认领");
            mTitles.add("已认领");

            for (int i = 0; i < mTitles.size(); i++) {
                mFragments.add(RefreshRecyclerFragment.newInstance("微心愿", i));
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
