/**
 *
 * 日    期：12-2-13
 */
package com.rop.validation;

import com.rop.*;
import com.rop.SecurityManager;
import com.rop.impl.DefaultSecurityManager;
import com.rop.impl.SimpleServiceMethodContext;
import com.rop.utils.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

/**
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopValidator implements RopValidator {


    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected SecurityManager securityManager = new DefaultSecurityManager();

    protected AppSecretManager appSecretManager = new FileBaseAppSecretManager();

    protected SessionChecker sessionChecker = new DefaultSessionChecker();

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

    @Override
    public MainError validate(ServiceMethodContext methodContext) {

        MainError mainError = null;

        //1.检查系统参数合法性
        mainError = validateSysParams(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //2.检查业务参数合法性
        mainError = validateBusinessParams(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //3.应用业务安全检查
        return  checkServiceAccessAllow(methodContext);
    }

    private MainError validateSysParams(ServiceMethodContext smc) {
        RopContext ropContext = smc.getRopContext();
        RopRequest ropRequest = smc.getRopRequest();

        //1.检查method参数
        if (smc.getMethod() == null) {
            return MainErrors.getError(MainErrorType.MISSING_METHOD, ropRequest.getLocale());
        } else {
            if (!ropContext.isValidMethod(smc.getMethod())) {
                return MainErrors.getError(MainErrorType.INVALID_METHOD, ropRequest.getLocale());
            }
        }

        //2.检查v参数
        if (smc.getVersion() == null) {
            return MainErrors.getError(MainErrorType.MISSING_VERSION, ropRequest.getLocale());
        } else {
            if (!ropContext.isValidMethodVersion(smc.getMethod(), smc.getVersion())) {
                return MainErrors.getError(MainErrorType.UNSUPPORTED_VERSION, ropRequest.getLocale());
            }
        }

        //3.检查appKey
        if (smc.getAppKey() == null)
            return MainErrors.getError(MainErrorType.MISSING_APP_KEY, ropRequest.getLocale());
        if (!appSecretManager.isValidAppKey(smc.getAppKey())) {
            return MainErrors.getError(MainErrorType.INVALID_APP_KEY, ropRequest.getLocale());
        }

        //4.检查sessionId
        MainError mainError = checkSession(smc);
        if (mainError != null) {
            return mainError;
        }

        //5.检查 format
        if (!MessageFormat.isValidFormat(smc.getFormat())) {
            return MainErrors.getError(MainErrorType.INVALID_FORMAT, ropRequest.getLocale());
        }

        //6.检查sign
        mainError = checkSign(smc);
        if (mainError != null) {
            return mainError;
        }

        return null;
    }

    @Override
    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    @Override
    public AppSecretManager getAppSecretManager() {
        return appSecretManager;
    }

    @Override
    public SessionChecker getSessionChecker() {
        return sessionChecker;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public void setAppSecretManager(AppSecretManager appSecretManager) {
        this.appSecretManager = appSecretManager;
    }

    public void setSessionChecker(SessionChecker sessionChecker) {
        this.sessionChecker = sessionChecker;
    }

    private MainError checkServiceAccessAllow(ServiceMethodContext methodContext) {
        RopRequest ropRequest = methodContext.getRopRequest();
        if (!getSecurityManager().isGranted(methodContext)) {
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

    private MainError validateBusinessParams(ServiceMethodContext methodContext) {
        List<ObjectError> errorList =
                (List<ObjectError>) methodContext.getAttribute(SimpleServiceMethodContext.SPRING_VALIDATE_ERROR_ATTRNAME);

        //将Bean数据绑定时产生的错误转换为Rop的错误
        if (errorList != null && errorList.size() > 0) {
            return toMainErrorOfSpringValidateErrors(errorList, methodContext.getLocale());
        }else {
            return null;
        }
    }

    /**
     * 检查签名的有效性
     *
     * @param smc
     * @return
     */
    private MainError checkSign(ServiceMethodContext smc) {

        //系统级签名开启,且服务方法需求签名
        if (smc.isSignEnable()) {
            if (!smc.getServiceMethodDefinition().isIgnoreSign()) {
                if (smc.getSign() == null) {
                    return MainErrors.getError(MainErrorType.MISSING_SIGNATURE, smc.getLocale());
                } else {
                    RopRequest ropRequest = smc.getRopRequest();
                    ArrayList<String> paramNames = new ArrayList<String>(ropRequest.getParamValues().keySet());
                    paramNames.removeAll(smc.getServiceMethodHandler().getIgnoreSignFieldNames());

                    HashMap<String, String> paramSingleValueMap = new HashMap<String, String>();
                    for (String paramName : paramNames) {
                        paramSingleValueMap.put(paramName, ropRequest.getParamValue(paramName));
                    }

                    //查看密钥是否存在，不存在则说明appKey是非法的
                    String signSecret = getAppSecretManager().getSecret(smc.getAppKey());
                    if (signSecret == null) {
                        throw new RopException("无法获取" + smc.getAppKey() + "对应的密钥");
                    }

                    String signValue = SignUtils.sign(paramNames, paramSingleValueMap, signSecret);
                    if (!signValue.equals(smc.getSign())) {
                        if (logger.isErrorEnabled()) {
                            logger.error(smc.getAppKey() + "的签名不合法，请检查");
                        }
                        return MainErrors.getError(MainErrorType.INVALID_SIGNATURE, smc.getLocale());
                    } else {
                        return null;
                    }
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn(smc.getMethod() + "忽略了签名");
                }
                return null;
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Rop关闭了签名检查,可通过将配置文件的“needCheckSign”开启。");
            }
            return null;
        }
    }


    /**
     * 是否是合法的会话
     *
     * @param sessionId
     * @return
     */
    private MainError checkSession(ServiceMethodContext smc) {
        //需要进行session检查
        if (smc.getServiceMethodHandler() != null &&
                smc.getServiceMethodHandler().getServiceMethodDefinition().isNeedInSession()) {
            if (smc.getSessionId() == null) {
                return MainErrors.getError(MainErrorType.MISSING_SESSION, null);
            } else {
                if (!getSessionChecker().isValid(smc.getSessionId())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(smc.getSessionId() + "会话不存在，请检查。");
                    }
                    return MainErrors.getError(MainErrorType.INVALID_SESSION, null);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private MainError toMainErrorOfSpringValidateErrors(List<ObjectError> allErrors, Locale locale) {
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

}

