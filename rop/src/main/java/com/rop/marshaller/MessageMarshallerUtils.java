/**
 * 日    期：12-5-29
 */
package com.rop.marshaller;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.rop.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * <pre>
 *   对请求响应的对象转成相应的报文。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class MessageMarshallerUtils {

    protected static final Logger logger = LoggerFactory.getLogger(MessageMarshallerUtils.class);

    private static final String UTF_8 = "utf-8";

    private static RopMarshaller xmlRopResponseMarshaller = new JaxbXmlRopMarshaller();

    private static RopMarshaller jsonRopResponseMarshaller = new JacksonJsonRopMarshaller();

    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

    static {
        SerializationConfig serializationConfig = jsonObjectMapper.getSerializationConfig();
        serializationConfig = serializationConfig.with(SerializationConfig.Feature.INDENT_OUTPUT).
                without(SerializationConfig.Feature.WRAP_ROOT_VALUE);
        jsonObjectMapper.setSerializationConfig(serializationConfig);
    }

    private static XmlMapper xmlObjectMapper = new XmlMapper();

    static {
        xmlObjectMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        xmlObjectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    }

    /**
     * 将请求对象转换为String
     *
     * @param request
     * @param format
     * @return
     */
    public static String getMessage(RopRequest request, MessageFormat format) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            if (format == MessageFormat.json) {
                if (request.getRopRequestContext() != null) {
                    jsonObjectMapper.writeValue(bos, request.getRopRequestContext().getAllParams());
                } else {
                    return "";
                }
            } else {
                if (request.getRopRequestContext() != null) {
                    xmlObjectMapper.writeValue(bos, request.getRopRequestContext().getAllParams());
                } else {
                    return "";
                }
            }
            return bos.toString(UTF_8);
        } catch (Exception e) {
            throw new RopException(e);
        }
    }

    /**
     * 将请求对象转换为String
     *
     * @param request
     * @param format
     * @return
     */
    public static String asUrlString(RopRequest request) {
        Map<String, String> allParams = request.getRopRequestContext().getAllParams();
        StringBuilder sb = new StringBuilder(256);
        boolean first = true;
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            first = false;
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }


    /**
     * 将{@link RopRequest}转换为字符串
     *
     * @param response
     * @param format
     * @return
     */
    public static String getMessage(RopResponse response, MessageFormat format) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            if (format == MessageFormat.json) {
                jsonRopResponseMarshaller.marshaller(response, bos);
            } else {
                xmlRopResponseMarshaller.marshaller(response, bos);
            }
            return bos.toString(UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RopException(e);
        }
    }

}

