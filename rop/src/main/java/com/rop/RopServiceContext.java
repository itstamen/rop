/**
 *
 * 日    期：12-2-27
 */
package com.rop;

import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *    处理服务请求的上下文。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopServiceContext {

    /**
     * 获取服务的方法
     *
     * @return
     */
    String getMethod();

    /**
     * 获取会话的ID
     *
     * @return
     */
    String getSessionId();

    /**
     * 获取本地化对象
     *
     * @return
     */
    Locale getLocale();

    /**
     * 获取服务的处理者
     *
     * @return
     */
    RopServiceHandler getRopServiceHandler();

    /**
     * 获取响应的格式
     *
     * @return
     */
    ResponseFormat getResponseFormat();

    /**
     * 获取对应的请求对象
     *
     * @return
     */
    HttpServletRequest getWebRequest();

    /**
     * 返回响应对象
     *
     * @return
     */
    RopResponse getRopResponse();

    /**
     * 请求对象
     *
     * @return
     */
    RopRequest getRopRequest();

    /**
     * @param ropRequest
     */
    void setRopRequest(RopRequest ropRequest);


    List<ObjectError> getAllErrors();

    void setAllErrors(List<ObjectError> allErrors);

    String getAppKey();

}

