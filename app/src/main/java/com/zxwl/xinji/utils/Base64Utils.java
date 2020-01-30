package com.zxwl.xinji.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * author：pc-20171125
 * data:2019/8/20 13:54
 */
public class Base64Utils {

    /**
     * 文件转Base64.
     * @param filePath
     * @return
     */
    public static String fileToBase(String filePath) {
        FileInputStream objFileIS = null;
        try {
            objFileIS = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
        byte[] byteBufferString = new byte[1024];
        try {
            for (int readNum; (readNum = objFileIS.read(byteBufferString)) != -1; ) {
                objByteArrayOS.write(byteBufferString, 0, readNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  Base64.encodeToString(objByteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static String bitmapToBase(String pathName) {
        //TODO 压缩成功后调用，返回压缩后的图片文件
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

}
