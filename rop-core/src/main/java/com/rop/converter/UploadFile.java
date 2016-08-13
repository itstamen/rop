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
package com.rop.converter;

import com.rop.annotation.IgnoreSign;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * <pre>
 *    上传的文件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@IgnoreSign
public class UploadFile {

    private String fileType;

    private byte[] content;

    /**
     * 根据文件内容构造
     *
     * @param content
     */
    public UploadFile(String fileType, byte[] content) {
        this.content = content;
        this.fileType = fileType;
    }

    /**
     * 根据文件构造
     * @param file
     */
    public UploadFile(File file) {
        try {
            this.content = FileUtils.readFileToByteArray(file);
            this.fileType = file.getName().substring(file.getName().lastIndexOf('.')+1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileType() {
        return fileType;
    }

    public byte[] getContent() {
        return content;
    }
}


