/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

