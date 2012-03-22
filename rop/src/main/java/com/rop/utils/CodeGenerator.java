/**
 * 版权所有 违者必究
 * by chenxh
 */
package com.rop.utils;

import org.springframework.util.Assert;

import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

/**
 * <br>
 * <b>类描述:</b> <br>
 * 负责生成各种编码。
 *
 * @since 1.0
 */
public class CodeGenerator {
    /**
     * 数英字符串
     */
    private final static String ALPHANUMERIC_STR;

    public static final String NUMBER_STR = "0123456789";

    static {
        String numberStr = NUMBER_STR;
        String aphaStr = "abcdefghijklmnopqrstuvwxyz";
        ALPHANUMERIC_STR = numberStr + aphaStr + aphaStr.toUpperCase();
    }

    private final static String PASSWORD = "0123456789";

    /**
     * 生成36个字符长度的UUID编码串，所有的字母转换为大写的格式。
     *
     * @return 36个字符长度的UUID。
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

    /**
     * 通过SHA算法对srcStr进行MD5编码（十六进制表示）
     *
     * @param srcStr 需要获取MD5的字符串，不能为null
     * @return srcStr的SHA代码（40个字符）
     */
    public static String getSHADigest(String srcStr) {
        return getDigest(srcStr, "SHA-1");
    }

    /**
     * 获取srcStr的MD5编码（十六进制表示）
     *
     * @param srcStr 需要获取MD5的字符串，不能为null
     * @return srcStr的MD5代码（32个字符）
     */
    public static String getMD5Digest(String srcStr) {
        return getDigest(srcStr, "MD5");
    }


    /**
     * 产生6位英数随机数,区分大小写
     *
     * @return
     */
    public static String getUpdateKey() {
        return getRandomStr(6);
    }

    /**
     * 产生一个随机英数字符串，区分大小定
     *
     * @param length 随机字符串的长度
     * @return
     */
    public static String getRandomStr(int len) {
        return getRandomStr(ALPHANUMERIC_STR, len);
    }

    /**
     * 返回length位的随机数字器
     *
     * @param length
     * @return
     */
    public static String getRandomNumberStr(int len) {
        return getRandomStr(NUMBER_STR, len);
    }

    private static String getRandomStr(String charSetStr, int len) {
        int srcStrLen = charSetStr.length();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            int maxnum = random.nextInt(1000);
            int result = maxnum % srcStrLen;
            char temp = charSetStr.charAt(result);
            sb.append(temp);
        }
        return sb.toString();
    }


    private static String getDigest(String srcStr, String alg) {
        Assert.notNull(srcStr);
        Assert.notNull(alg);
        try {
            MessageDigest alga = MessageDigest
                    .getInstance(alg);
            alga.update(srcStr.getBytes());
            byte[] digesta = alga.digest();
            return byte2hex(digesta);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 二进制转十六进制字符串
     *
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0");
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }


    public static void main(String[] args) {
        System.out.println(getUUID());
        System.out.println(getSHADigest("111111"));
        System.out.println(getRandomStr(10));
        System.out.println(getRandomStr(8));
        System.out.println(getUpdateKey());
        System.out.println(getRandomPassword());
    }

    /**
     * 获得随机密码，6位数字。
     *
     * @return
     */
    public static String getRandomPassword() {
        int srcStrLen = PASSWORD.length();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int maxnum = random.nextInt(1000);
            int result = maxnum % srcStrLen;
            char temp = PASSWORD.charAt(result);
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * UUID取模.
     *
     * @param uuidStr
     * @param mod
     * @return
     */
    public static int getModWithUUID(String uuidStr, int mod) {
        UUID uuid = UUID.fromString(uuidStr);
        long value = Math.abs(uuid.getLeastSignificantBits());
        return (int) (value % mod);
    }
}
