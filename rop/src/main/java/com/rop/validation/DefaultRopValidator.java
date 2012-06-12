/**
 *
 * 日    期：12-2-13
 */
package com.rop.validation;

import com.rop.*;
import com.rop.SecurityManager;
import com.rop.annotation.HttpAction;
import com.rop.impl.DefaultSecurityManager;
import com.rop.impl.SimpleRequestContext;
import com.rop.utils.RopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

/**
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
    public MainError validateSysparams(RequestContext methodContext) {

        MainError mainError = null;

        //1.检查系统参数合法性
        mainError = validateSysParams(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //2.检查请求HTTP方法的匹配性
        mainError = validateHttpAction(methodContext);
        if (mainError != null) {
            return mainError;
        }

        return mainError;
    }

    @Override
    public MainError validateOther(RequestContext methodContext) {

        MainError mainError = null;

        //1.检查业务参数合法性
        mainError = validateBusinessParams(methodContext);
        if (mainError != null) {
            return mainError;
        }

        //2.应用业务安全检查
        return checkServiceAccessAllow(methodContext);
    }

    /**
     * 校验是否是合法的HTTP动作
     *
     * @param methodContext
     */
    private MainError validateHttpAction(RequestContext methodContext) {
        MainError mainError = null;
        HttpAction[] httpActions = methodContext.getServiceMethodDefinition().getHttpAction();
        if (httpActions.length > 0) {
            boolean isValid = false;
            for (HttpAction httpAction : httpActions) {
                if (httpAction == methodContext.getHttpAction()) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                mainError = MainErrors.getError(MainErrorType.HTTP_ACTION_NOT_ALLOWED, methodContext.getLocale());
            }
        }
        return mainError;
    }

    private MainError validateSysParams(RequestContext rc) {

        RopContext ropContext = rc.getRopContext();

        //2.检查method参数
        if (rc.getMethod() == null) {
            return MainErrors.getError(MainErrorType.MISSING_METHOD, rc.getLocale());
        } else {
            if (!ropContext.isValidMethod(rc.getMethod())) {
                return MainErrors.getError(MainErrorType.INVALID_METHOD, rc.getLocale());
            }
        }

        //3.检查v参数
        if (rc.getVersion() == null) {
            return MainErrors.getError(MainErrorType.MISSING_VERSION, rc.getLocale());
        } else {
            if (!ropContext.isValidMethodVersion(rc.getMethod(), rc.getVersion())) {
                return MainErrors.getError(MainErrorType.UNSUPPORTED_VERSION, rc.getLocale());
            }
        }

        //4.检查appKey
        if (rc.getAppKey() == null)
            return MainErrors.getError(MainErrorType.MISSING_APP_KEY, rc.getLocale());
        if (!appSecretManager.isValidAppKey(rc.getAppKey())) {
            return MainErrors.getError(MainErrorType.INVALID_APP_KEY, rc.getLocale());
        }

        //5.检查sessionId
        MainError mainError = checkSession(rc);
        if (mainError != null) {
            return mainError;
        }

        //6.检查 format
        if (!MessageFormat.isValidFormat(rc.getFormat())) {
            return MainErrors.getError(MainErrorType.INVALID_FORMAT, rc.getLocale());
        }

        //7.检查sign
        mainError = checkSign(rc);
        if (mainError != null) {
            return mainError;
        }

        return null;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public AppSecretManager getAppSecretManager() {
        return appSecretManager;
    }


    public SessionChecker getSessionChecker() {
        return sessionChecker;
    }

    @Override
    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    public void setAppSecretManager(AppSecretManager appSecretManager) {
        this.appSecretManager = appSecretManager;
    }

    @Override
    public void setSessionChecker(SessionChecker sessionChecker) {
        this.sessionChecker = sessionChecker;
    }

    private MainError checkServiceAccessAllow(RequestContext smc) {
        if (!getSecurityManager().isGranted(smc)) {
            MainError mainError = SubErrors.getMainError(SubErrorType.ISV_INVALID_PERMISSION, smc.getLocale());
            SubError subError = SubErrors.getSubError(SubErrorType.ISV_INVALID_PERMISSION.value(),
                    SubErrorType.ISV_INVALID_PERMISSION.value(),
                    smc.getLocale());
            mainError.addSubError(subError);
            if (mainError != null && logger.isErrorEnabled()) {
                logger.debug("安全检查管理器禁止调用将服务。");
            }
            return mainError;
        } else {
            return null;
        }
    }

    private MainError validateBusinessParams(RequestContext methodContext) {
        List<ObjectError> errorList =
                (List<ObjectError>) methodContext.getAttribute(SimpleRequestContext.SPRING_VALIDATE_ERROR_ATTRNAME);

        //将Bean数据绑定时产生的错误转换为Rop的错误
        if (errorList != null && errorList.size() > 0) {
            return toMainErrorOfSpringValidateErrors(errorList, methodContext.getLocale());
        } else {
            return null;
        }
    }

    /**
     * 检查签名的有效性
     *
     * @param smc
     * @return
     */
    private MainError checkSign(RequestContext smc) {

        //系统级签名开启,且服务方法需求签名
        if (smc.isSignEnable()) {
            if (!smc.getServiceMethodDefinition().isIgnoreSign()) {
                if (smc.getSign() == null) {
                    return MainErrors.getError(MainErrorType.MISSING_SIGNATURE, smc.getLocale());
                } else {
                    ArrayList<String> paramNames = new ArrayList<String>(smc.getAllParams().keySet());
                    paramNames.removeAll(smc.getServiceMethodHandler().getIgnoreSignFieldNames());

                    HashMap<String, String> paramSingleValueMap = new HashMap<String, String>();
                    for (String paramName : paramNames) {
                        paramSingleValueMap.put(paramName, smc.getParamValue(paramName));
                    }

                    //查看密钥是否存在，不存在则说明appKey是非法的
                    String signSecret = getAppSecretManager().getSecret(smc.getAppKey());
                    if (signSecret == null) {
                        throw new RopException("无法获取" + smc.getAppKey() + "对应的密钥");
                    }

                    String signValue = RopUtils.sign(paramNames, paramSingleValueMap, signSecret);
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
    private MainError checkSession(RequestContext smc) {
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

