package com.zxwl.frame.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zxwl.frame.R;
import com.zxwl.frame.adapter.AddSiteLeftAdapter;
import com.zxwl.frame.adapter.AddSiteRightAdapter;
import com.zxwl.frame.adapter.onRecyclerClick;
import com.zxwl.network.bean.response.DepartmentBean;

import java.util.HashSet;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * author：pc-20171125
 * data:2019/11/27 18:10
 */
public class SelectSitePopupWindow extends BasePopupWindow {
    private RecyclerView rvLeft;
    private RecyclerView rvRight;

    private AddSiteLeftAdapter leftAdapter;
    private AddSiteRightAdapter rightAdapter;

    private List<DepartmentBean> leftData;
    private List<DepartmentBean> rightData;

    private TextView tvAddCancle;
    private TextView tvAddConfirm;

    public SelectSitePopupWindow(Context context,
                                 int width,
                                 int height,
                                 List<DepartmentBean> leftData,
                                 List<DepartmentBean> rightData) {
        super(context, width, height);

        this.leftData = leftData;
        this.rightData = rightData;


        findViews();
    }

    public SelectSitePopupWindow(Context context,
                                 int width,
                                 int height,
                                 List<DepartmentBean> leftData,
                                 List<DepartmentBean> rightData,
                                 boolean isShowCancle) {
        this(context, width, height, leftData, rightData);
    }

    public void setLeftNewData(List<DepartmentBean> newLeftData) {
        leftAdapter.replaceData(newLeftData);
    }

    public void setRightNewData(List<DepartmentBean> newRightData) {
        rightAdapter.replaceData(newRightData);
        rvRight.smoothScrollToPosition(0);
    }

    public void setLeftSelectIndex(int position) {
//        leftAdapter.setSelectIndex(position);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_add_site);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        selectSite.clear();
    }

    //左边列表上次选中的下标
    private int leftLastSelectIndex = -1;

    private HashSet<DepartmentBean> selectSite = new HashSet<>();

    public HashSet<DepartmentBean> getSelectSite() {
        return selectSite;
    }

    /**
     * 清空选中状态
     */
    public void clearCheckStatus() {
        for (int i = 1, len = leftAdapter.getItemCount(); i < len; i++) {
            leftAdapter.getDatas().get(i).isCheck = false;
        }

        for (int i = 1, len = rightAdapter.getItemCount(); i < len; i++) {
            rightAdapter.getDatas().get(i).isCheck = false;
        }

        leftAdapter.notifyDataSetChanged();
        rightAdapter.notifyDataSetChanged();
    }

    /**
     * 设置账户级别
     * @param level
     */
    public void setLevel(int level){
        leftAdapter.setLevel(level);
    }

    /**
     * 创建view
     *
     * @return
     */
    private void findViews() {
        rvLeft = (RecyclerView) findViewById(R.id.rv_left);
        rvRight = (RecyclerView) findViewById(R.id.rv_right);

        tvAddCancle = (TextView) findViewById(R.id.tv_add_cancle);
        tvAddConfirm = (TextView) findViewById(R.id.tv_add_confirm);

        leftAdapter = new AddSiteLeftAdapter(getContext(), leftData);
        rightAdapter = new AddSiteRightAdapter(getContext(), rightData);

        leftAdapter.setRecyclerClick(new onRecyclerClick() {
            @Override
            public void onClick(int position) {
                if (null != screenClick) {
                    DepartmentBean item = leftAdapter.getDatas().get(position);
                    screenClick.onLeftClick(item.id);
                }
            }

            @Override
            public void onCheck(int position) {
                DepartmentBean departmentBean = leftAdapter.getDatas().get(position);
                departmentBean.isCheck = !departmentBean.isCheck;

                if ("全部".equals(departmentBean.departmentName)) {
                    for (int i = 1; i < leftAdapter.getDatas().size(); i++) {
                        leftAdapter.getDatas().get(i).isCheck = departmentBean.isCheck;
                        if (departmentBean.isCheck) {
                            selectSite.add(leftAdapter.getDatas().get(i));
                        } else {
                            selectSite.remove(leftAdapter.getDatas().get(i));
                        }
                    }
                } else {
                    //判断是否选中添加bean
                    if (departmentBean.isCheck) {
                        selectSite.add(departmentBean);
                    } else {
                        selectSite.remove(departmentBean);
                    }
                }

                //当前列表全选
                boolean allSelect = true;

                for (int i = 1; i < leftAdapter.getItemCount(); i++) {
                    if (!leftAdapter.getDatas().get(i).isCheck) {
                        allSelect = false;
                        break;
                    }
                }

                if (allSelect) {
                    leftAdapter.getDatas().get(0).isCheck = true;
                } else {
                    leftAdapter.getDatas().get(0).isCheck = false;
                }
                leftAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDelete(int position) {

            }
        });

        rightAdapter.setRecyclerClick(new onRecyclerClick() {
            @Override
            public void onCheck(int position) {
                DepartmentBean departmentBean = rightAdapter.getDatas().get(position);
                departmentBean.isCheck = !departmentBean.isCheck;

                if ("全部".equals(departmentBean.departmentName)) {
                    for (int i = 1; i < rightAdapter.getDatas().size(); i++) {
                        rightAdapter.getDatas().get(i).isCheck = departmentBean.isCheck;
                        if (departmentBean.isCheck) {
                            selectSite.add(rightAdapter.getDatas().get(i));
                        } else {
                            selectSite.remove(rightAdapter.getDatas().get(i));
                        }
                    }
                } else {
                    //判断是否选中添加bean
                    if (departmentBean.isCheck) {
                        selectSite.add(departmentBean);
                    } else {
                        selectSite.remove(departmentBean);
                    }
                }

                //当前列表全选
                boolean allSelect = true;

                for (int i = 1; i < rightAdapter.getItemCount(); i++) {
                    if (!rightAdapter.getDatas().get(i).isCheck) {
                        allSelect = false;
                        break;
                    }
                }

                if (allSelect) {
                    rightAdapter.getDatas().get(0).isCheck = true;
                } else {
                    rightAdapter.getDatas().get(0).isCheck = false;
                }

                rightAdapter.notifyDataSetChanged();
            }

            @Override
            public void onClick(int position) {

            }

            @Override
            public void onDelete(int position) {

            }
        });

        tvAddCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvAddConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != screenClick) {
                    if (selectSite.size() <= 0) {
                        Toast.makeText(getContext(), "请选择参会列表", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuffer terUris = new StringBuffer();
                    StringBuffer siteIds = new StringBuffer();
                    for (DepartmentBean departmentBean : selectSite) {
                        siteIds.append(departmentBean.siteId + ",");
                        terUris.append(departmentBean.terUri + ",");
                    }

                    screenClick.onConfirmClick(terUris.toString(), siteIds.toString());
                }
            }
        });

        rvLeft.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRight.setLayoutManager(new LinearLayoutManager(getContext()));

        rvLeft.setAdapter(leftAdapter);
        rvRight.setAdapter(rightAdapter);
    }

    public interface onScreenClick {
        void onLeftClick(int cityId);

        void onLeftCheck(int cityId);

        void onRightClick(int cityId);

        void onConfirmClick(String selectSiteUri, String selectSiteId);
    }

    private onScreenClick screenClick;

    public void setOnScreenClick(onScreenClick screenClick) {
        this.screenClick = screenClick;
    }
}