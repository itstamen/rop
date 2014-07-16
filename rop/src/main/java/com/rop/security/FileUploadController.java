/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-8-1
 */
package com.rop.security;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface FileUploadController {

    /**
     * 上传文件的类型是否是允许
     * @param fileType
     * @return
     */
    boolean isAllowFileType(String fileType);

    /**
     * 是否超过了上传大小的限制
     * @param fileSize
     * @return
     */
    boolean isExceedMaxSize(int fileSize);

    /**
     * 获取支持上传的文件格式
     * @return
     */
    String getAllowFileTypes();

    /**
     * 获取最大的文件大小，单位为K
     * @return
     */
    int getMaxSize();
}

