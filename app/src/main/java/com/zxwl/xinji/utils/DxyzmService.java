package com.zxwl.xinji.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * author：pc-20171125
 * data:2019/5/31 13:29
 */
public class DxyzmService {
    /**********需要准备的参数**************/
    public static String accessKey = "LTAILk58aGdpcqcS";//需要修改
    public static String accessSecret = "";//需要修改
    public static String code = "SMS_41635111";//需要修改
    public static String signName = "测试99";//需要修改

    private static final String SYMBOLS = "0123456789"; // 数字

    // 字符串
    // private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final Random RANDOM = new SecureRandom();

    /**
     * 获取长度为 4 的随机数字
     *
     * @return 随机数字
     */
    public static String getCode() {
        // 如果需要4位，那 new char[4] 即可，其他位数同理可得
        char[] nonceChars = new char[4];

        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

}
