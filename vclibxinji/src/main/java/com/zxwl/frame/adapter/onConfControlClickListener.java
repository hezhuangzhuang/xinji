package com.zxwl.frame.adapter;

/**
 * author：pc-20171125
 * data:2018/7/11 17:34
 */
public interface onConfControlClickListener {
    void onClick(int position);

    void onHangUp(int position);

    void onMic(int position);

    void onBroadcast(int position);

    void onCall(int position);
}
