/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-8-3
 */
package com.rop.request;

import org.apache.commons.codec.binary.Base64;

/**
 * <pre>
 *     Rop的上传文件编码格式为：
 *   fileType@BASE64编码的文件内容
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class UploadFileUtils {

    public static final char SPERATOR = '@';

    /**
     * 获取文件的类型
     *
     * @param encodeFile
     * @return
     */
    public static final String getFileType(String encodeFile) {
        int speratorIndex = encodeFile.indexOf(SPERATOR);
        if (speratorIndex > -1) {
            String fileType = encodeFile.substring(0, speratorIndex);
            return fileType.toLowerCase();
        } else {
            throw new IllegalUploadFileFormatException("文件格式不对，正确格式为：<文件格式>@<文件内容>");
        }
    }

    /**
     * 获取文件的字节数组
     *
     * @param encodeFile
     * @return
     */
    public static final byte[] decode(String encodeFile) {
        int speratorIndex = encodeFile.indexOf(SPERATOR);
        if (speratorIndex > -1) {
            String content = encodeFile.substring(speratorIndex + 1);
            return Base64.decodeBase64(content);
        } else {
            throw new IllegalUploadFileFormatException("文件格式不对，正确格式为：<文件格式>@<文件内容>");
        }
    }

    /**
     * 将文件编码为BASE64的字符串
     *
     * @param bytes
     * @return
     */
    public static final String encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 将文件编码为一个字符串
     * @param uploadFile
     * @return
     */
    public static final String encode(UploadFile uploadFile){
        StringBuilder sb = new StringBuilder();
        sb.append(uploadFile.getFileType());
        sb.append(SPERATOR);
        sb.append(encode(uploadFile.getContent()));
        return sb.toString();
    }
}

