/**
 *
 * 日    期：12-2-13
 */
package com.rop.validation;

import com.rop.RopException;
import com.rop.RopRequest;
import com.rop.RopServiceContext;
import com.rop.utils.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.IOException;
import java.util.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopValidator implements RopValidator {

    private static Logger logger = LoggerFactory.getLogger(DefaultRopValidator.class);

    private static final Set SUPPORT_VERSIONS = new HashSet<String>();

    private static final Set SUPPORT_FORMATS = new HashSet<String>();

    private SessionChecker sessionChecker;

    private AppSecretManager appSecretManager;

    static {
        SUPPORT_VERSIONS.add("1.0");

        SUPPORT_FORMATS.add("xml");
        SUPPORT_FORMATS.add("json");
    }

    private static final Map<String, SubErrorType> INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS = new LinkedHashMap<String, SubErrorType>();
    public static final String SIGN_SECRET_OF_ZTT = "SEPU!PWO@LVE&045#67$";

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

    public DefaultRopValidator() {
        init();
    }

    private void init() {
        Properties properties = null;

        try {
            properties = PropertiesLoaderUtils.loadAllProperties("rop.conf.properties");
        } catch (IOException e) {
            throw new RopException("在类路径下找不到rop.properties的配置文件", e);
        }
        String sessionCheckerImplClassName = properties.getProperty(
                "SessionChecker.implClass", "com.rop.validation.DefaultSessionChecker");
        String appSecretManagerImplClassName = properties.getProperty(
                "AppSecretManager.implClass", "com.rop.validation.FileBaseAppSecretManager");
        try {
            Class<?> sessionCheckerClass = ClassUtils.forName(sessionCheckerImplClassName, this.getClass().getClassLoader());
            this.sessionChecker = (SessionChecker) BeanUtils.instantiateClass(sessionCheckerClass);
            Class<?> appSecretManagerImplClass = ClassUtils.forName(appSecretManagerImplClassName, this.getClass().getClassLoader());
            this.appSecretManager = (AppSecretManager) BeanUtils.instantiateClass(appSecretManagerImplClass);
        } catch (ClassNotFoundException e) {
            throw new RopException("请检查根类路径或com/stamen/rop的类路径的rop.properties中的配置", e);
        }
    }

    public MainError validate(RopServiceContext context) {

        MainError mainError = null;

        //1.格式格式
        if (mainError == null) {
            RopRequest ropRequest = context.getRopRequest();
            mainError = validateRopRequest(ropRequest);
            if (mainError == null && (context.getAllErrors() != null && context.getAllErrors().size() > 0)) {
                mainError = parseBeanConstraintError(context.getAllErrors(), ropRequest.getLocale());
            }
        }

        //2.签名检查
        if(mainError == null){
            checkSign(context);
        }

        //3.会话检查
        if (mainError == null) {
            mainError = checkSession(context);
        }


        return mainError;
    }

    public SessionChecker getSessionChecker() {
        return this.sessionChecker;
    }

    public AppSecretManager getAppSecretManager() {
        return this.appSecretManager;
    }

    /**
     * 检查签名的有效性
     *
     * @param context
     * @return
     */
    private MainError checkSign(RopServiceContext context) {
        ArrayList<String> paramNames = new ArrayList<String>(context.getWebRequest().getParameterMap().keySet());
        paramNames.removeAll(context.getRopServiceHandler().getIgnoreSignFieldNames());

        HashMap<String, String> paramSingleValueMap = new HashMap<String, String>();
        for (String paramName : paramNames) {
            paramSingleValueMap.put(paramName, context.getWebRequest().getParameter(paramName));
        }
        String signSecret = getSignSecret(context.getAppKey());

        if(signSecret == null){
            throw new RopException("无法获取"+context.getAppKey()+"对应的密钥");
        }
        String signValue = sign(paramNames, paramSingleValueMap,signSecret);
        if (!signValue.equals(context.getWebRequest().getParameter("sign"))) {
            return MainErrors.getError(MainErrorType.INVALID_SIGNATURE, context.getLocale());
        } else {
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
        return CodeGenerator.getSHADigest(sb.toString()).toUpperCase();
    }

    protected String getSignSecret(String appKey) {
        return this.appSecretManager.getSecret(appKey);
    }

    /**
     * 是否是合法的会话
     *
     * @param sessionId
     * @return
     */
    private MainError checkSession(RopServiceContext context) {
        MainError mainError = null;
        if (context.getRopServiceHandler().isNeedInSession() && !isValidSession(context.getSessionId())) {
            mainError = MainErrors.getError(MainErrorType.INVALID_SESSION, null);
        }
        return mainError;
    }

    protected boolean isValidSession(String sessionId) {
        return this.sessionChecker.isValid(sessionId);
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

            if (!isValidFormat(ropRequest.getFormat())) {
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

