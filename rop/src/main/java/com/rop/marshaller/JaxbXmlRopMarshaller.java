/**
 *
 * 日    期：12-2-27
 */
package com.rop.marshaller;

import com.rop.RopException;
import com.rop.RopMarshaller;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *    将对象流化成XML，每个类型对应一个{@link JAXBContext}，{@link JAXBContext} 是线程安全的，但是
 * {@link Marshaller}是非线程安全的，因此需要每次创建一个。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JaxbXmlRopMarshaller implements RopMarshaller {

    private static Map<Class, JAXBContext> jaxbContextHashMap = new ConcurrentHashMap<Class, JAXBContext>();

    public void marshaller(Object object, OutputStream outputStream) {
        try {
            Marshaller m = buildMarshaller(object.getClass());
            m.marshal(object, outputStream);
        } catch (JAXBException e) {
            throw new RopException(e);
        }
    }


    private Marshaller buildMarshaller(Class<?> objectType) throws JAXBException {
        if (!jaxbContextHashMap.containsKey(objectType)) {
            JAXBContext context = JAXBContext.newInstance(objectType);
            jaxbContextHashMap.put(objectType, context);
        }
        JAXBContext context = jaxbContextHashMap.get(objectType);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        return marshaller;
    }
}

