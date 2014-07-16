/**
 *
 * 日    期：12-2-13
 */
package com.rop.security;

import com.rop.*;
import com.rop.annotation.HttpAction;
import com.rop.config.SystemParameterNames;
import com.rop.impl.DefaultServiceAccessController;
import com.rop.impl.SimpleRopRequestContext;
import com.rop.request.UploadFileUtils;
import com.rop.session.SessionManager;
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
public class DefaultSecurityManager implements SecurityManager {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected ServiceAccessController serviceAccessController = new DefaultServiceAccessController();

    protected AppSecretManager appSecretManager = new FileBaseAppSecretManager();

    protected SessionManager sessionManager;

    protected InvokeTimesController invokeTimesController;

    protected FileUploadController fileUploadController;

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


    public MainError validateSystemParameters(RopRequestContext context) {
        RopContext ropContext = context.getRopContext();
        MainError mainError = null;

        //1.检查appKey
        if (context.getAppKey() == null) {
            return MainErrors.getError(MainErrorType.MISSING_APP_KEY, context.getLocale(),
                                       context.getMethod(),context.getVersion(),
                                       SystemParameterNames.getAppKey());
        }
        if (!appSecretManager.isValidAppKey(context.getAppKey())) {
            return MainErrors.getError(MainErrorType.INVALID_APP_KEY, context.getLocale(),
                                       context.getMethod(),context.getVersion(),
                                       context.getAppKey());
        }


        //2.检查会话
        mainError = checkSession(context);
        if (mainError != null) {
            return mainError;
        }

        //3.检查method参数
        if (context.getMethod() == null) {
            return MainErrors.getError(MainErrorType.MISSING_METHOD, context.getLocale(),
                                       SystemParameterNames.getMethod());
        } else {
            if (!ropContext.isValidMethod(context.getMethod())) {
                return MainErrors.getError(MainErrorType.INVALID_METHOD,
                        context.getLocale(),context.getMethod());
            }
        }

        //4.检查v参数
        if (context.getVersion() == null) {
            return MainErrors.getError(MainErrorType.MISSING_VERSION, context.getLocale(),
                                       context.getMethod(),
                                       SystemParameterNames.getVersion());
        } else {
            if (!ropContext.isValidVersion(context.getMethod(), context.getVersion())) {
                return MainErrors.getError(
                        MainErrorType.UNSUPPORTED_VERSION, context.getLocale(),
                        context.getMethod(), context.getVersion());
            }
        }

        //5.检查签名正确性
        mainError = checkSign(context);
        if (mainError != null) {
            return mainError;
        }

        //6.检查服务方法的版本是否已经过期
        if (context.getServiceMethodDefinition().isObsoleted()) {
            return MainErrors.getError(MainErrorType.METHOD_OBSOLETED, context.getLocale(),
                    context.getMethod(), context.getVersion());
        }

        //7.检查请求HTTP方法的匹配性
        mainError = validateHttpAction(context);
        if (mainError != null) {
            return mainError;
        }

        //8.检查 format
        if (!MessageFormat.isValidFormat(context.getFormat())) {
            return MainErrors.getError(MainErrorType.INVALID_FORMAT, context.getLocale(),
                                        context.getMethod(),context.getVersion(),context.getFormat());
        }

        return null;
    }


    public MainError validateOther(RopRequestContext rrctx) {

        MainError mainError = null;

        //1.判断应用/用户是否有权访问目标服务
        mainError = checkServiceAccessAllow(rrctx);
        if (mainError != null) {
            return mainError;
        }

        //2.判断应用/会话/用户访问服务的次数或频度是否超限
        mainError = checkInvokeTimesLimit(rrctx);
        if (mainError != null) {
            return mainError;
        }

        //3.如果是上传文件的服务，检查文件类型和大小是否满足要求
        mainError = checkUploadFile(rrctx);
        if (mainError != null) {
            return mainError;
        }

        //4.检查业务参数合法性
        mainError = validateBusinessParams(rrctx);
        if (mainError != null) {
            return mainError;
        }

        return null;
    }

    private MainError checkUploadFile(RopRequestContext rrctx) {
        ServiceMethodHandler serviceMethodHandler = rrctx.getServiceMethodHandler();
        if (serviceMethodHandler != null && serviceMethodHandler.hasUploadFiles()) {
            List<String> fileFieldNames = serviceMethodHandler.getUploadFileFieldNames();
            for (String fileFieldName : fileFieldNames) {
                String paramValue = rrctx.getParamValue(fileFieldName);
                if (paramValue != null) {
                    if (paramValue.indexOf("@") < 0) {
                        return MainErrors.getError(
                                MainErrorType.UPLOAD_FAIL, rrctx.getLocale(),
                                rrctx.getMethod(), rrctx.getVersion(), "MESSAGE_VALID:not contain '@'.");
                    } else {
                        String fileType = UploadFileUtils.getFileType(paramValue);
                        if (!fileUploadController.isAllowFileType(fileType)) {
                            return MainErrors.getError(
                                    MainErrorType.UPLOAD_FAIL, rrctx.getLocale(),
                                    rrctx.getMethod(), rrctx.getVersion(),
                                    "FILE_TYPE_NOT_ALLOW:the valid file types is:" + fileUploadController.getAllowFileTypes());
                        }
                        byte[] fileContent = UploadFileUtils.decode(paramValue);
                        if (fileUploadController.isExceedMaxSize(fileContent.length)) {
                            return MainErrors.getError(
                                    MainErrorType.UPLOAD_FAIL, rrctx.getLocale(),
                                    rrctx.getMethod(), rrctx.getVersion(),
                                    "EXCEED_MAX_SIZE:" + fileUploadController.getMaxSize() + "k");
                        }
                    }
                }
            }
        }
        return null;
    }


    public void setInvokeTimesController(InvokeTimesController invokeTimesController) {
        this.invokeTimesController = invokeTimesController;
    }


    public void setServiceAccessController(ServiceAccessController serviceAccessController) {
        this.serviceAccessController = serviceAccessController;
    }


    public void setAppSecretManager(AppSecretManager appSecretManager) {
        this.appSecretManager = appSecretManager;
    }


    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    public void setFileUploadController(FileUploadController fileUploadController) {
        this.fileUploadController = fileUploadController;
    }

    private MainError checkInvokeTimesLimit(RopRequestContext rrctx) {
        if (invokeTimesController.isAppInvokeFrequencyExceed(rrctx.getAppKey())) {
            return MainErrors.getError(MainErrorType.EXCEED_APP_INVOKE_FREQUENCY_LIMITED, rrctx.getLocale());
        } else if (invokeTimesController.isAppInvokeLimitExceed(rrctx.getAppKey())) {
            return MainErrors.getError(MainErrorType.EXCEED_APP_INVOKE_LIMITED, rrctx.getLocale());
        } else if (invokeTimesController.isSessionInvokeLimitExceed(rrctx.getAppKey(), rrctx.getSessionId())) {
            return MainErrors.getError(MainErrorType.EXCEED_SESSION_INVOKE_LIMITED, rrctx.getLocale());
        } else if (invokeTimesController.isUserInvokeLimitExceed(rrctx.getAppKey(), rrctx.getSession())) {
            return MainErrors.getError(MainErrorType.EXCEED_USER_INVOKE_LIMITED, rrctx.getLocale());
        } else {
            return null;
        }
    }

    /**
     * 校验是否是合法的HTTP动作
     *
     * @param ropRequestContext
     */
    private MainError validateHttpAction(RopRequestContext ropRequestContext) {
        MainError mainError = null;
        HttpAction[] httpActions = ropRequestContext.getServiceMethodDefinition().getHttpAction();
        if (httpActions.length > 0) {
            boolean isValid = false;
            for (HttpAction httpAction : httpActions) {
                if (httpAction == ropRequestContext.getHttpAction()) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                mainError = MainErrors.getError(
                        MainErrorType.HTTP_ACTION_NOT_ALLOWED, ropRequestContext.getLocale(),
                        ropRequestContext.getMethod(), ropRequestContext.getVersion(),
                        ropRequestContext.getHttpAction());
            }
        }
        return mainError;
    }

    public ServiceAccessController getServiceAccessController() {
        return serviceAccessController;
    }

    public AppSecretManager getAppSecretManager() {
        return appSecretManager;
    }

    private MainError checkServiceAccessAllow(RopRequestContext smc) {
        if (!getServiceAccessController().isAppGranted(smc.getAppKey(), smc.getMethod(), smc.getVersion())) {
            MainError mainError = SubErrors.getMainError(SubErrorType.ISV_INVALID_PERMISSION, smc.getLocale());
            SubError subError = SubErrors.getSubError(SubErrorType.ISV_INVALID_PERMISSION.value(),
                    SubErrorType.ISV_INVALID_PERMISSION.value(),
                    smc.getLocale());
            mainError.addSubError(subError);
            if (mainError != null && logger.isErrorEnabled()) {
                logger.debug("未向ISV开放该服务的执行权限(" + smc.getMethod() + ")");
            }
            return mainError;
        } else {
            if (!getServiceAccessController().isUserGranted(smc.getSession(), smc.getMethod(), smc.getVersion())) {
                MainError mainError = MainErrors.getError(
                        MainErrorType.INSUFFICIENT_USER_PERMISSIONS, smc.getLocale(),
                        smc.getMethod(), smc.getVersion());
                SubError subError = SubErrors.getSubError(SubErrorType.ISV_INVALID_PERMISSION.value(),
                        SubErrorType.ISV_INVALID_PERMISSION.value(),
                        smc.getLocale());
                mainError.addSubError(subError);
                if (mainError != null && logger.isErrorEnabled()) {
                    logger.debug("未向会话用户开放该服务的执行权限(" + smc.getMethod() + ")");
                }
                return mainError;
            }
            return null;
        }
    }

    private MainError validateBusinessParams(RopRequestContext context) {
        List<ObjectError> errorList =
                (List<ObjectError>) context.getAttribute(SimpleRopRequestContext.SPRING_VALIDATE_ERROR_ATTRNAME);

        //将Bean数据绑定时产生的错误转换为Rop的错误
        if (errorList != null && errorList.size() > 0) {
            return toMainErrorOfSpringValidateErrors(errorList, context.getLocale(),context);
        } else {
            return null;
        }
    }

    /**
     * 检查签名的有效性
     *
     * @param context
     * @return
     */
    private MainError checkSign(RopRequestContext context) {

        //系统级签名开启,且服务方法需求签名
        if (context.isSignEnable()) {
            if (!context.getServiceMethodDefinition().isIgnoreSign()) {
                if (context.getSign() == null) {
                    return MainErrors.getError(MainErrorType.MISSING_SIGNATURE, context.getLocale(),
                                               context.getMethod(),context.getVersion(),
                                               SystemParameterNames.getSign());
                } else {

                    //获取需要签名的参数
                    List<String> ignoreSignFieldNames = context.getServiceMethodHandler().getIgnoreSignFieldNames();
                    HashMap<String, String> needSignParams = new HashMap<String, String>();
                    for (Map.Entry<String, String> entry : context.getAllParams().entrySet()) {
                        if (!ignoreSignFieldNames.contains(entry.getKey())) {
                            needSignParams.put(entry.getKey(), entry.getValue());
                        }
                    }

                    //查看密钥是否存在，不存在则说明appKey是非法的
                    String signSecret = getAppSecretManager().getSecret(context.getAppKey());
                    if (signSecret == null) {
                        throw new RopException("无法获取" + context.getAppKey() + "对应的密钥");
                    }

                    String signValue = RopUtils.sign(needSignParams, signSecret);
                    if (!signValue.equals(context.getSign())) {
                        if (logger.isErrorEnabled()) {
                            logger.error(context.getAppKey() + "的签名不合法，请检查");
                        }
                        return MainErrors.getError(
                                MainErrorType.INVALID_SIGNATURE, context.getLocale(),
                                context.getMethod(),context.getVersion());
                    } else {
                        return null;
                    }
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn(context.getMethod() + "忽略了签名");
                }
                return null;
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.warn("{}{}服务方法未开启签名", context.getMethod(), context.getVersion());
            }
            return null;
        }
    }


    /**
     * 是否是合法的会话
     *
     * @param context
     * @return
     */
    private MainError checkSession(RopRequestContext context) {
        //需要进行session检查
        if (context.getServiceMethodHandler() != null &&
                context.getServiceMethodHandler().getServiceMethodDefinition().isNeedInSession()) {
            if (context.getSessionId() == null) {
                return MainErrors.getError(MainErrorType.MISSING_SESSION, context.getLocale(),
                        context.getMethod(), context.getVersion(), SystemParameterNames.getSessionId());
            } else {
                if (!isValidSession(context)) {
                    return MainErrors.getError(MainErrorType.INVALID_SESSION, context.getLocale(),
                            context.getMethod(), context.getVersion(),context.getSessionId());
                }
            }
        }
        return null;
    }

    private boolean isValidSession(RopRequestContext smc) {
        if (sessionManager.getSession(smc.getSessionId()) == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(smc.getSessionId() + "会话不存在，请检查。");
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 将通过JSR 303框架校验的错误转换为Rop的错误体系
     *
     * @param allErrors
     * @param locale
     * @return
     */
    private MainError toMainErrorOfSpringValidateErrors(
            List<ObjectError> allErrors, Locale locale,RopRequestContext context) {
        if (hastSubErrorType(allErrors, SubErrorType.ISV_MISSING_PARAMETER)) {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_MISSING_PARAMETER,context);
        } else if (hastSubErrorType(allErrors, SubErrorType.ISV_PARAMETERS_MISMATCH)) {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_PARAMETERS_MISMATCH,context);
        } else {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_INVALID_PARAMETE,context);
        }
    }

    /**
     * 判断错误列表中是否包括指定的子错误
     *
     * @param allErrors
     * @param subErrorType1
     * @return
     */
    private boolean hastSubErrorType(List<ObjectError> allErrors, SubErrorType subErrorType1) {
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                if (INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.containsKey(fieldError.getCode())) {
                    SubErrorType tempSubErrorType = INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.get(fieldError.getCode());
                    if (tempSubErrorType == subErrorType1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 生成对应子错误的错误类
     *
     * @param allErrors
     * @param locale
     * @param subErrorType
     * @return
     */
    private MainError getBusinessParameterMainError(
            List<ObjectError> allErrors, Locale locale, SubErrorType subErrorType,RopRequestContext context) {
        MainError mainError = SubErrors.getMainError(subErrorType, locale,context.getMethod(),context.getVersion());
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                SubErrorType tempSubErrorType = INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.get(fieldError.getCode());
                if (tempSubErrorType == subErrorType) {
                    String subErrorCode =
                            SubErrors.getSubErrorCode(
                                    tempSubErrorType, fieldError.getField(), fieldError.getRejectedValue());
                    SubError subError = SubErrors.getSubError(subErrorCode, tempSubErrorType.value(), locale,
                            fieldError.getField(), fieldError.getRejectedValue());
                    mainError.addSubError(subError);
                }
            }
        }
        return mainError;
    }
}

