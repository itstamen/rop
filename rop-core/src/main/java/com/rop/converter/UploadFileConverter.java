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

/**
 * <pre>
 *   将以BASE64位编码字符串转换为字节数组的{@link UploadFile}对象
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class UploadFileConverter implements RopConverter<String, UploadFile> {


    public UploadFile convert(String source) {
        String fileType = UploadFileUtils.getFileType(source);
        byte[] contentBytes = UploadFileUtils.decode(source);
        return new UploadFile(fileType, contentBytes);
    }


    public String unconvert(UploadFile target) {
        return UploadFileUtils.encode(target);
    }


    public Class<String> getSourceClass() {
        return String.class;
    }


    public Class<UploadFile> getTargetClass() {
        return UploadFile.class;
    }
}

