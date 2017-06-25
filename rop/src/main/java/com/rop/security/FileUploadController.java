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

