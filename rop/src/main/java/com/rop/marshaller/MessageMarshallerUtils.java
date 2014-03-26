/**
 * 日    期：12-5-29
 */
package com.rop.marshaller;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.rop.MessageFormat;
import com.rop.RopException;
import com.rop.RopMarshaller;
import com.rop.RopRequest;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

    private static RopMarshaller xmlRopResponseMarshaller = new JaxbXmlRopMarshaller();

    static {
        SerializationConfig serializationConfig = jsonObjectMapper.getSerializationConfig();
        serializationConfig = serializationConfig.without(SerializationConfig.Feature.WRAP_ROOT_VALUE)
                .with(SerializationConfig.Feature.INDENT_OUTPUT);
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
     * @param allParams
     * @return
     */
    public static String asUrlString(Map<String,String> allParams) {
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
     * @param object
     * @param format
     * @return
     */
    public static String getMessage(Object object, MessageFormat format) {
        if (object == null) {
            return "NONE MSG";
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        try {
            if (format == MessageFormat.json) {
                JsonGenerator jsonGenerator = jsonObjectMapper.getJsonFactory().createJsonGenerator(bos, JsonEncoding.UTF8);
                jsonObjectMapper.writeValue(jsonGenerator, object);
            } else {
                xmlRopResponseMarshaller.marshaller(object, bos);
            }
            return bos.toString(UTF_8);
        } catch (Throwable e) {
            throw new RopException(e);
        } finally {
            try {
                bos.close();
            } catch (IOException ee) {
                logger.error("消息转换异常", ee);
            }
        }
    }

}

