package com.rop.sign;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.rop.RopException;
import com.rop.config.SystemParameterNames;
import com.rop.security.AppSecretManager;
import com.rop.utils.RopUtils;

/**
 * hmac消息摘要数字签名处理实现
 * @author liangruisen
 */
public class HmacDigestSignHandler extends DigestSignHandler {

	public HmacDigestSignHandler() {
		super("HmacSHA1", null);
	}

	public HmacDigestSignHandler(AppSecretManager appSecretManager) {
		super("HmacSHA1", appSecretManager);
	}

	public HmacDigestSignHandler(String algorithm, AppSecretManager appSecretManager) {
		super(algorithm, appSecretManager);
	}

	/**
	 * 产生签名
	 * @see com.rop.sign.DigestSignHandler#sign(java.util.Map, java.util.Collection)
	 */
	@Override
	public String sign(Map<String, String> paramValues, Collection<String> ignore) {
		try {
			String signContent = RopUtils.getSignContent(paramValues, ignore);
			String appKey = paramValues.get(SystemParameterNames.getAppKey());
			String secret = appSecretManager.getSecret(appKey);
			byte[] sha1Digest = getDigest(secret, signContent);
			return byte2hex(sha1Digest);
		} catch (IOException e) {
			throw new RopException(e);
		}
	}

	/**
	 * 获取消息摘要
	 * @param secret
	 * @param data
	 * @return
	 * @throws IOException
	 */
	protected byte[] getDigest(String secret, String data) throws IOException {
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
