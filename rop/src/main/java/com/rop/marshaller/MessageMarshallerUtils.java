/**
 * 日    期：12-5-29
 */
package com.rop.marshaller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rop.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * <pre>
 *
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
        serializationConfig = serializationConfig.with(SerializationConfig.Feature.INDENT_OUTPUT);
        jsonObjectMapper.setSerializationConfig(serializationConfig);
    }

    private static XmlMapper xmlObjectMapper = new XmlMapper();

    static {
//        xmlObjectMapper.configure(SerializationFeature.INDENT_OUTPUT,true);
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
                jsonObjectMapper.writeValue(bos, request.getParamValues());
            } else {
                xmlObjectMapper.writeValue(bos, request.getParamValues());
            }
            return bos.toString(UTF_8);
        } catch (Exception e) {
            throw new RopException(e);
        }
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

