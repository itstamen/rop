/**
 *
 * 日    期：12-2-13
 */
package com.rop.validation;

import com.rop.*;
import com.rop.impl.SimpleServiceMethodContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.security.MessageDigest;
import java.util.*;

/**
 * <pre>
 *    对请求数据、会话合法性进行校验，产生相应的错误信息。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopValidator implements RopValidator {

    private static Logger logger = LoggerFactory.getLogger(DefaultRopValidator.class);

    private static final Set SUPPORT_VERSIONS = new HashSet<String>();

    private static final Set SUPPORT_FORMATS = new HashSet<String>();

    private static final String SIGN = "sign";

    private RopContext ropContext;

    static {
        SUPPORT_VERSIONS.add("1.0");

        SUPPORT_FORMATS.add("xml");
        SUPPORT_FORMATS.add("json");
    }

    private static final Map<String, SubErrorType> INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS = new LinkedHashMap<String, SubErrorType>();

    static {
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("typeMismatch", SubErrorType.ISV_PARAMETERS_MISMATCH);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("NotNull", SubErrorType.ISV_MISSING_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("NotEmpty", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Size", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Range", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Pattern", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Min", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Max", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("DecimalMin", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("DecimalMax", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Digits", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Past", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Future", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("AssertFalse", SubErrorType.ISV_INVALID_PARAMETE);
    }

    public DefaultRopValidator(RopContext ropContext) {
        this.ropContext = ropContext;
    }

    public MainError validate(ServiceMethodContext methodContext) {

        MainError mainError = null;

        //1.校验appKey的正确性
        mainError = checkAppKey(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //2.签名检查
        mainError = checkSign(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //3.会话检查
        mainError = checkSession(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //4.安全检查
        mainError = checkSecurity(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //5.校验请求参数格式合法性
        mainError = checkParamConstraints(methodContext);

        return mainError;
    }

    private MainError checkSecurity(ServiceMethodContext methodContext) {
        RopRequest ropRequest = methodContext.getRopRequest();
        if (!ropContext.getRopConfig().getSecurityManager().isGranted(methodContext)) {
            MainError mainError = SubErrors.getMainError(SubErrorType.ISV_INVALID_PERMISSION, ropRequest.getLocale());
            SubError subError = SubErrors.getSubError(SubErrorType.ISV_INVALID_PERMISSION.value(),
                    SubErrorType.ISV_INVALID_PERMISSION.value(),
                    ropRequest.getLocale());
            mainError.addSubError(subError);
            if (mainError != null && logger.isErrorEnabled()) {
                logger.debug("安全检查管理器禁止调用将服务。");
            }
            return mainError;
        } else {
            return null;
        }
    }

    private MainError checkAppKey(ServiceMethodContext methodContext) {
        String secret = ropContext.getRopConfig().getAppSecretManager().getSecret(methodContext.getAppKey());
        if (secret == null) {
            MainError mainError = MainErrors.getError(MainErrorType.INVALID_APP_KEY, methodContext.getLocale());
            if (mainError != null && logger.isErrorEnabled()) {
                logger.error(methodContext.getAppKey() + "不是合法的appKey，请检查。");
            }
            return mainError;
        } else {
            return null;
        }
    }

    private MainError checkParamConstraints(ServiceMethodContext methodContext) {

        MainError mainError = validateRopRequest(methodContext.getRopRequest());

        List<ObjectError> errorList =
                (List<ObjectError>) methodContext.getAttribute(SimpleServiceMethodContext.SPRING_VALIDATE_ERROR_ATTRNAME);

        if (mainError == null && (errorList != null && errorList.size() > 0)) {
            mainError = parseBeanConstraintError(errorList, methodContext.getLocale());
        }
        return mainError;
    }

    /**
     * 检查签名的有效性
     *
     * @param methodContext
     * @return
     */
    private MainError checkSign(ServiceMethodContext methodContext) {
        if (methodContext.getRopContext().getRopConfig().isNeedCheckSign()) {
            RopRequest ropRequest = methodContext.getRopRequest();
            ArrayList<String> paramNames = new ArrayList<String>(ropRequest.getParamValues().keySet());
            paramNames.removeAll(methodContext.getServiceMethodHandler().getIgnoreSignFieldNames());

            HashMap<String, String> paramSingleValueMap = new HashMap<String, String>();
            for (String paramName : paramNames) {
                paramSingleValueMap.put(paramName, ropRequest.getParamValue(paramName));
            }
            String signSecret = getSignSecret(methodContext.getAppKey());

            if (signSecret == null) {
                throw new RopException("无法获取" + methodContext.getAppKey() + "对应的密钥");
            }
            String signValue = sign(paramNames, paramSingleValueMap, signSecret);
            if (!signValue.equals(ropRequest.getParamValue(SIGN))) {
                if (logger.isErrorEnabled()) {
                    logger.error(methodContext.getAppKey() + "的签名不合法，请检查");
                }
                return MainErrors.getError(MainErrorType.INVALID_SIGNATURE, methodContext.getLocale());
            } else {
                return null;
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("未开启签名校验,可通过将配置文件的“needCheckSign”开启。");
            }
            return null;
        }
    }

    /**
     * 使用<code>secret</code>对paramValues按以下算法进行签名： <br/>
     * uppercase(hex(sha1(secretkey1value1key2value2...secret))
     *
     * @param paramNames
     * @param paramValues
     * @param secret
     * @return
     */
    public static String sign(List<String> paramNames, Map<String, String> paramValues, String secret) {
        StringBuilder sb = new StringBuilder();
        Collections.sort(paramNames);
        sb.append(secret);
        for (String paramName : paramNames) {
            sb.append(paramName).append(paramValues.get(paramName));
        }
        sb.append(secret);
        return getSHA1Digest(sb.toString()).toUpperCase();
    }

    private static String getSHA1Digest(String srcStr) {
        Assert.notNull(srcStr);
        try {
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            alga.update(srcStr.getBytes());
            byte[] digesta = alga.digest();
            return byte2hex(digesta);
        } catch (Exception e) {
            logger.error("签名操作发生错误", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 二进制转十六进制字符串
     *
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0");
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    protected String getSignSecret(String appKey) {
        return ropContext.getRopConfig().getAppSecretManager().getSecret(appKey);
    }

    /**
     * 是否是合法的会话
     *
     * @param sessionId
     * @return
     */
    private MainError checkSession(ServiceMethodContext methodContext) {
        if (methodContext.getServiceMethodHandler().getServiceMethodDefinition().isNeedInSession()) {
            if (!isValidSession(methodContext.getSessionId())) {
                if (logger.isErrorEnabled()) {
                    logger.error(methodContext.getSessionId() + "会话不存在，请检查。");
                }
                return MainErrors.getError(MainErrorType.INVALID_SESSION, null);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean isValidSession(String sessionId) {
        return ropContext.getRopConfig().getSessionChecker().isValid(sessionId);
    }

    private MainError validateRopRequest(RopRequest ropRequest) {
        if (ropRequest == null) {
            return MainErrors.getError(MainErrorType.INVALID_ARGUMENTS, ropRequest.getLocale());
        } else {
            if (ropRequest.getAppKey() == null) {
                return MainErrors.getError(MainErrorType.MISSING_APP_KEY, ropRequest.getLocale());
            }

            if (ropRequest.getV() == null) {
                return MainErrors.getError(MainErrorType.MISSING_VERSION, ropRequest.getLocale());
            } else {
                if (!isValidVersion(ropRequest.getV())) {
                    return MainErrors.getError(MainErrorType.UNSUPPORTED_VERSION, ropRequest.getLocale());
                }
            }

            if (!isValidFormat(ropRequest.getMsgFormat())) {
                return MainErrors.getError(MainErrorType.INVALID_FORMAT, ropRequest.getLocale());
            }

            if (ropRequest.getSign() == null) {
                return MainErrors.getError(MainErrorType.MISSING_SIGNATURE, ropRequest.getLocale());
            }
        }
        return null;
    }

    private MainError parseBeanConstraintError(List<ObjectError> allErrors, Locale locale) {
        MainError mainError = MainErrors.getError(MainErrorType.INVALID_ARGUMENTS, locale);
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                if (INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.containsKey(fieldError.getCode())) {
                    SubErrorType subErrorType = INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.get(fieldError.getCode());
                    String subErrorCode = null;
                    switch (subErrorType) {
                        case ISV_MISSING_PARAMETER:
                        case ISV_INVALID_PARAMETE:
                            subErrorCode = SubErrors.getSubErrorCode(subErrorType, fieldError.getField());
                            break;
                        case ISV_PARAMETERS_MISMATCH:
                            subErrorCode = SubErrors.getSubErrorCode(subErrorType, fieldError.getField(), fieldError.getRejectedValue());
                            break;
                        default:
                            subErrorCode = SubErrors.getSubErrorCode(subErrorType, fieldError.getField());
                    }
                    SubError subError = SubErrors.getSubError(subErrorCode, subErrorType.value(), locale, fieldError.getField(), fieldError.getRejectedValue());
                    mainError.addSubError(subError);
                } else {
                    String subErrorCode = SubErrors.getSubErrorCode(SubErrorType.ISV_INVALID_PARAMETE, fieldError.getField());
                    SubError subError = SubErrors.getSubError(subErrorCode, SubErrorType.ISV_INVALID_PARAMETE.value(), locale, fieldError.getField());
                    mainError.addSubError(subError);
                }
            }
        }
        return mainError;
    }

    private boolean isValidVersion(String version) {
        return SUPPORT_VERSIONS.contains(version);
    }

    private boolean isValidFormat(String format) {
        return SUPPORT_FORMATS.contains(format);
    }
}

