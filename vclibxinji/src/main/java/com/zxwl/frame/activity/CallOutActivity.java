package com.zxwl.frame.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallMgr;
import com.zxwl.frame.R;

/**
 * 去电界面
 */
public class CallOutActivity extends BaseMediaActivity {

    @Override
    protected void setListener() {
        ((TextView) findViewById(R.id.tv_number)).setText(TextUtils.isEmpty(String.valueOf(mCallNumber)) ? "" : String.valueOf(mCallNumber));

        findViewById(R.id.tv_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CallMgr.getInstance().endCall(mCallID);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(CallOutActivity.this, "异常是:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((TextView) findViewById(R.id.tv_number)).setText(TextUtils.isEmpty(mCallNumber) ? "" : mCallNumber);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_call_out;
    }

}
