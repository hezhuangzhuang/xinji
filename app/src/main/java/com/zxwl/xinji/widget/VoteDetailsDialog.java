package com.zxwl.xinji.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zxwl.network.bean.response.VotePeopleDetailsBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.ImageListAdapter;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * author：pc-20171125
 * data:2019/10/18 09:43
 */
public class VoteDetailsDialog extends BasePopupWindow implements View.OnClickListener {
    private FrameLayout flClose;
    private TextView tvTitle;
    private TextView tvNumber;
    private TextView tvPoll;
    private TextView tvRanking;
    private TextView tvDisparity;
    private TextView tvContent;
    private TextView btDialogVote;

    private RecyclerView rvList;

    private ImageListAdapter imageListAdapter;
    private List<String> images;

    private VotePeopleDetailsBean votePeopleBean;

    public VoteDetailsDialog(Context context,
                             int width,
                             int height,
                             boolean delayInit,
                             VotePeopleDetailsBean votePeopleBean) {
        super(context, width, height, delayInit);

        findViews();
        setPopupGravity(Gravity.BOTTOM);

        tvTitle.setText(votePeopleBean.name);
        tvNumber.setText(votePeopleBean.order + "号");
        tvPoll.setText(String.valueOf(votePeopleBean.num));
        tvRanking.setText(String.valueOf(votePeopleBean.ranking));
        tvContent.setText(votePeopleBean.introduction);

        btDialogVote.setVisibility(votePeopleBean.isVote ? View.GONE : View.VISIBLE);

        initRecycler(votePeopleBean);
    }

    private void initRecycler(VotePeopleDetailsBean votePeopleBean) {
        images = new ArrayList<>();

        addUrl(votePeopleBean.pic1);
        addUrl(votePeopleBean.pic2);
        addUrl(votePeopleBean.pic3);
        addUrl(votePeopleBean.pic4);
        addUrl(votePeopleBean.pic5);

        imageListAdapter = new ImageListAdapter(R.layout.item_img, images);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(imageListAdapter);
    }

    private void addUrl(String pic) {
        if (!TextUtils.isEmpty(pic)) {
            images.add(pic);
        }
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_vote_details);
    }

    private void findViews() {
        flClose = (FrameLayout) findViewById(R.id.fl_close);
        btDialogVote = (TextView) findViewById(R.id.tv_dialog_vote);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvPoll = (TextView) findViewById(R.id.tv_poll);
        tvRanking = (TextView) findViewById(R.id.tv_ranking);
        tvDisparity = (TextView) findViewById(R.id.tv_disparity);
        tvContent = (TextView) findViewById(R.id.tv_content);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        flClose.setOnClickListener(this);
        btDialogVote.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_close:
                dismiss();
                break;

            case R.id.tv_dialog_vote:
                if (null != voteListener) {
                    voteListener.onVoteClick();
                }
                break;
        }
    }

    public interface onVoteListener {
        void onVoteClick();
    }

    private onVoteListener voteListener;

    public void setVoteListener(onVoteListener voteListener) {
        this.voteListener = voteListener;
    }
}
