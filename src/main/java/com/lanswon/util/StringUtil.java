package com.lanswon.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;

/**
 * 包含了一些加密算法等
 */
public class StringUtil {

    // 加密类型
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA-1";

    /**
     * 支持中文UTF-8
     *
     * @param str  需要加密的串
     * @param type 加密类型 ,可通过 StringUtil.MD5,StringUtil.SHA1获取常量值
     * @return
     */
    public static String getEncode(String str, String type) {
        StringBuffer hexstr = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            md.update(str.getBytes("UTF-8"));
            byte[] digest = md.digest();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            System.out.println("getEncode:" + e.getMessage());
            return str;
        }
    }

    /**
     * 获取输入流对应的加密串
     *
     * @param inputStream
     * @return
     */
    public static String getEncodeStream(InputStream inputStream) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = inputStream.read(buffer)) > 0) {
                mdTemp.update(buffer, 0, numRead);
            }
            return toHexString(mdTemp.digest());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取对应文件的加密串
     *
     * @param fileName
     * @return
     */
    public static String getEncodeFile(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
            return getEncodeStream(inputStream);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 返回去除"-"的32位UUID
     *
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }

    /**
     * 用SHA1算法生成安全签名
     *
     * @param token     票据
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return 安全签名
     */
    public static String getSHA1(String token, String timestamp, String nonce) {
        try {
            String[] array = new String[]{token, timestamp, nonce};
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 3; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            return getEncode(str, "SHA-1");
        } catch (Exception e) {
            System.out.println("sha1error:" + e.getMessage());
            return null;
        }
    }

    /**
     * 腾讯优图MD5,不支持中文
     *
     * @param str
     * @return
     */
    public static String toString(String str, String type) {
        try {
            byte[] strTemp = str.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance(type);
            mdTemp.update(strTemp);
            return toHexString(mdTemp.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private static String toHexString(byte[] md) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int j = md.length;
        char str[] = new char[j * 2];
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
            str[i * 2 + 1] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
