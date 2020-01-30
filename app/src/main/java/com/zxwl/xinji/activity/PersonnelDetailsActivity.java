package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.CircleImageView;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.PartyBranchPersonBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员详情界面
 */
public class PersonnelDetailsActivity extends BaseActivity {
    public static final String PERSONNEL_BEAN = "PERSONNEL_BEAN";
    public static final String IS_PARTY_MEMBER = "IS_PARTY_MEMBER";

    //是否是书记
    public static final String IS_SECRETARY = "IS_SECRETARY";
    public static final String UNIT_ID = "UNIT_ID";

    private PartyBranchPersonBean personBean;

    private ImageView ivBackOperate;
    private CircleImageView ivHead;
    private TextView tvTopName;
    private TextView tvName;
    private TextView tvSex;
    private TextView tvAge;
    private TextView tvPhone;
    private TextView tvDzzPost;
    private TextView tvCwhPost;
    private TextView tvTime;

    private TextView tvSixLable;
    private TextView tvSevenLable;
    private TextView tvEightLable;


    //是否是党员
    private boolean isPartyMember = false;

    //是否是书记
    private boolean isSecretary = false;


    //单位id
    private int unitId;

    public static void startActivity(Context context,
                                     PartyBranchPersonBean branchPersonBean,
                                     boolean isPartyMember) {
        Intent intent = new Intent(context, PersonnelDetailsActivity.class);
        intent.putExtra(PERSONNEL_BEAN, branchPersonBean);
        intent.putExtra(IS_PARTY_MEMBER, isPartyMember);
        context.startActivity(intent);
    }

    public static void startActivity(Context context,
                                     int unitId,
                                     boolean isSecretary) {
        Intent intent = new Intent(context, PersonnelDetailsActivity.class);
        intent.putExtra(UNIT_ID, unitId);
        intent.putExtra(IS_SECRETARY, isSecretary);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        ivHead = (CircleImageView) findViewById(R.id.iv_head);
        tvTopName = (TextView) findViewById(R.id.tv_top_name);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvDzzPost = (TextView) findViewById(R.id.tv_dzz_post);
        tvCwhPost = (TextView) findViewById(R.id.tv_cwh_post);
        tvTime = (TextView) findViewById(R.id.tv_time);

        tvSixLable = (TextView) findViewById(R.id.tv_six_lable);
        tvSevenLable = (TextView) findViewById(R.id.tv_seven_lable);
        tvEightLable = (TextView) findViewById(R.id.tv_eight_lable);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null);

        personBean = (PartyBranchPersonBean) getIntent().getSerializableExtra(PERSONNEL_BEAN);
        isPartyMember = getIntent().getBooleanExtra(IS_PARTY_MEMBER, false);


        isSecretary = getIntent().getBooleanExtra(IS_SECRETARY, false);
        unitId = getIntent().getIntExtra(UNIT_ID, 0);

        //如果是书记需要请求数据
        if (isSecretary) {
            getPersonData();
        } else {
            setPersonContent(personBean);
        }

    }

    private void setPersonContent(PartyBranchPersonBean personBean) {
        tvTopName.setText(personBean.name);
        tvName.setText(personBean.name);

        tvAge.setText(personBean.age);
        tvPhone.setText(personBean.telephone);

        Glide.with(this)
                .asBitmap()
                .apply(new RequestOptions().error(R.mipmap.ic_minel_head))
                .load(personBean.pic)
                .into(ivHead);

        if (isPartyMember) {
            tvAge.setText(personBean.birthdayStr);

            tvSex.setText(personBean.sexVal);
            tvSixLable.setText("职务");
            tvSevenLable.setText("入党时间");

            //职务
            tvDzzPost.setText(personBean.postsVal);
            //入党时间
            tvCwhPost.setText(DateUtil.longToString(personBean.addpartyDate, DateUtil.FORMAT_DATE_CHINA));

            tvEightLable.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
        } else {
            tvAge.setText(personBean.birthday);
            tvSex.setText(personBean.sexValue);

            tvDzzPost.setText(personBean.orgPositionValue);
            tvCwhPost.setText(personBean.villagePositionValue);
            tvTime.setText(DateUtil.longToString(personBean.workingTime, DateUtil.FORMAT_DATE_CHINA));
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personnel_details;
    }

    private void getPersonData() {
        Map<String, String> hashMap = new HashMap<>();
        if ( 0 != unitId) {
            hashMap.put("unitId", String.valueOf(unitId));
        }

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryPartyPerson(Urls.QUERY_ZBSJ_LIST, hashMap)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<PartyBranchPersonBean>>() {
                    @Override
                    public void onSuccess(BaseData<PartyBranchPersonBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<PartyBranchPersonBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                setPersonContent(dataList.get(0));
                            }
                        } else {
                            //隐藏骨架图
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }
}
