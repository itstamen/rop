package com.rop.sign;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;

import com.rop.config.SystemParameterNames;
import com.rop.security.AppSecretManager;
import com.rop.utils.RopUtils;

/**
 * RSA数字签名
 */
public class RsaDigestSignHandler extends DigestSignHandler {

	private static final String CACHE_NAME = RsaDigestSignHandler.class.getName();

	/**
	 * 平台证书私钥
	 */
	private PrivateKey privateKey;

	/**
	 * 平台证书私钥base64字符串
	 */
	private String privateKeyData;

	/**
	 *  平台证书私钥文件
	 */
	private Resource privateKeyLocation;

	private AppSecretManager appSecretManager;

	private CacheManager cacheManager;
	
	private String cacheName = CACHE_NAME;

	@PostConstruct
	public void init() {
		if (privateKey == null && StringUtils.isNotBlank(privateKeyData)) {
			try{
				privateKey = getPrivateKeyFromPKCS8("RSA", new ByteArrayInputStream(privateKeyData.getBytes()));
			}catch(RuntimeException e){
				throw e;
			}catch(Exception e){
				throw new IllegalArgumentException(e);
			}
		}
		if (privateKey == null && privateKeyLocation != null) {
			InputStream input = null;
			try {
				input = privateKeyLocation.getInputStream();
				privateKey = getPrivateKeyFromPKCS8("RSA", input);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}finally{
				IOUtils.closeQuietly(input);
			}
		}
	}
	
	/**
	 * 获取平台的私钥
	 * @return PrivateKey
	 */
	public PrivateKey getPrivateKey(){
		if(privateKey == null){
			init();
		}
		if (privateKey == null) {
			throw new IllegalArgumentException(
					"privateKey or privateKeyData or privateKeyLocation argument is required; it must not be null");
		}
		return privateKey;
	}
	
	/**
	 * 从输入流中读取证书的私钥
	 * @param algorithm
	 * @param ins
	 * @return PrivateKey
	 * @throws Exception
	 */
	public PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins) throws Exception {
		if (ins == null || StringUtils.isEmpty(algorithm)) {
			return null;
		}
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		byte[] encodedKey = IOUtils.toByteArray(ins);
		encodedKey = Base64.decodeBase64(encodedKey);
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	}
	
	/**
	 * 从输入流中读取证书的公钥
	 * @param algorithm
	 * @param ins
	 * @return PublicKey
	 * @throws Exception
	 */
	public PublicKey getPublicKeyFromPKCS8(String algorithm, InputStream ins) throws Exception {
		if (ins == null || StringUtils.isEmpty(algorithm)) {
			return null;
		}
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		byte[] encodedKey = IOUtils.toByteArray(ins);
		encodedKey = Base64.decodeBase64(encodedKey);
		return keyFactory.generatePublic(new PKCS8EncodedKeySpec(encodedKey));
	}
	
	/**
	 * 根据appKey获取客户端的公钥
	 * @param appKey
	 * @return PublicKey
	 * @throws Exception
	 */
	public PublicKey getPublicKey(String appKey) throws Exception{
		PublicKey key = getPublicKeyFromCache(appKey);
		if(key != null){
			return key;
		}
		if (appSecretManager == null) {
			throw new IllegalArgumentException("appSecretManager argument is required; it must not be null");
		}
		String keyData = appSecretManager.getSecret(appKey);
		key = getPublicKeyFromPKCS8("RSA", new ByteArrayInputStream(keyData.getBytes()));
		putPublicKeyToCache(appKey, key);
		return key;
	}
	
	/**
	 * 将客户端的公钥放入到缓存中
	 * @param appKey
	 * @param key
	 */
	private void putPublicKeyToCache(String appKey, PublicKey key){
		if(cacheManager == null){
			return;
		}
		try{
			Cache cache = cacheManager.getCache(cacheName);
			if(cache == null){
				return;
			}
			cache.put(appKey, key);
		}catch(Exception e){
		}
	}
	
	/**
	 * 从缓存中获取客户端的公钥
	 * @param appKey
	 * @return PublicKey
	 */
	private PublicKey getPublicKeyFromCache(String appKey){
		if(cacheManager == null){
			return null;
		}
		try{
			Cache cache = cacheManager.getCache(cacheName);
			if(cache == null){
				return null;
			}
			ValueWrapper wrapper = cache.get(appKey);
			return (PublicKey) (wrapper == null ? null : wrapper.get());
		}catch(Exception e){
		}
		return null;
	}

	/**
	 * 产生签名
	 * @see com.rop.sign.DigestSignHandler#sign(java.util.Map, java.util.Collection)
	 */
	@Override
	public String sign(Map<String, String> paramValues, Collection<String> ignore) {
		String signValue = RopUtils.getSignContent(paramValues, ignore);
		try{
			Signature signature = Signature.getInstance("SHA1WithRSA");
			signature.initSign(getPrivateKey());
			signature.update(signValue.getBytes());
			return Base64.encodeBase64String(signature.sign());
		}catch(Exception e){
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 检查签名是否正确
	 * @see com.rop.sign.DigestSignHandler#signCheck(java.lang.String, java.util.Map, java.util.Collection)
	 */
	@Override
	public boolean signCheck(String sign, Map<String, String> paramMap, Collection<String> ignore) {
		String signValue = RopUtils.getSignContent(paramMap, ignore);
		try{
			String appKey = paramMap.get(SystemParameterNames.getAppKey());
			Signature signature = Signature.getInstance("SHA1WithRSA");
			signature.initVerify(getPublicKey(appKey));
			signature.update(signValue.getBytes());
			return signature.verify(Base64.decodeBase64(sign));
		}catch(Exception e){
			throw new IllegalArgumentException(e);
		}
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public void setPrivateKeyData(String privateKeyData) {
		this.privateKeyData = privateKeyData;
	}

	public void setPrivateKeyLocation(Resource privateKeyLocation) {
		this.privateKeyLocation = privateKeyLocation;
	}

	@Autowired(required=false)
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@javax.annotation.Resource
	public void setAppSecretManager(AppSecretManager appSecretManager) {
		this.appSecretManager = appSecretManager;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
}