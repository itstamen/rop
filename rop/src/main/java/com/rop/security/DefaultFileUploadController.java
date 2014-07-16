/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-8-2
 */
package com.rop.security;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *    1.如果maxSize为非正数，则表示不限制大小；
 *    2.如果allowAllTypes为true表示不限制文件类型；
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultFileUploadController implements FileUploadController {

    private List<String> fileTypes;
    
    private int maxSize = -1;

    private boolean allowAllTypes = false;

    public DefaultFileUploadController(int maxSize) {
        this.allowAllTypes = true;
        this.maxSize = maxSize;
    }

    /**
     * @param fileTypes
     * @param maxSize 最大文件大小，单位为k
     */
    public DefaultFileUploadController(List<String> fileTypes, int maxSize) {
        ArrayList<String> tempFileTypes = new ArrayList<String>(fileTypes.size());
        for (String fileType : fileTypes) {
            tempFileTypes.add(fileType.toLowerCase());
        }
        this.fileTypes = tempFileTypes;
        this.maxSize = maxSize;
    }


    public boolean isAllowFileType(String fileType) {
        if(allowAllTypes){
            return true;
        }else{
            if(fileType == null){
                return false;
            }else{
                fileType = fileType.toLowerCase();
                return fileTypes.contains(fileType);
            }
        }
    }


    public boolean isExceedMaxSize(int fileSize) {
        if(maxSize > 0){
            return fileSize > maxSize * 1024;
        }else{
            return false;
        }
    }


    public String getAllowFileTypes() {
        if (CollectionUtils.isEmpty(fileTypes)) {
            return "";
        }else{
            StringBuilder sb = new StringBuilder();
            String seprator = "";
            for (String fileType : fileTypes) {
                sb.append(seprator);
                sb.append(fileType);
                seprator = ",";
            }
            return sb.toString();
        }
    }


    public int getMaxSize() {
        return this.maxSize;
    }
}

