/**
 * Copyright 2012-2017 the original author or authors.
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
package com.rop.client.sign;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.rop.RopException;
import com.rop.utils.RopUtils;

/**
 * hmac消息摘要数字签名处理实现
 * @author liangruisen
 *
 */
public class HmacDigestSignHandler extends DigestSignHandler {

	public HmacDigestSignHandler() {
		super("HmacSHA1", null);
	}

	public HmacDigestSignHandler(String secret) {
		super("HmacSHA1", secret);
	}

	public HmacDigestSignHandler(String algorithm, String secret) {
		super(algorithm, secret);
	}

	/**
	 * 产生签名
	 * @see com.rop.client.sign.DigestSignHandler#sign(java.util.Map, java.util.Collection)
	 */
	@Override
	public String sign(Map<String, String> paramValues, Collection<String> ignore) {
		try {
			String signContent = RopUtils.getSignContent(paramValues, ignore);
			byte[] sha1Digest = getDigest(signContent);
			return byte2hex(sha1Digest);
		} catch (IOException e) {
			throw new RopException(e);
		}
	}

	/**
	 * 获取消息摘要
	 * @param data
	 * @return
	 */
	protected byte[] getDigest(String data) throws IOException {
		byte[] bytes = null;
		try {
			SecretKey secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			bytes = mac.doFinal(data.getBytes());
		} catch (GeneralSecurityException gse) {
			throw new IOException(gse.toString());
		}
		return bytes;
	}
}
