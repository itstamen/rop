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
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JaxbXmlRopMarshaller implements RopMarshaller {

    private static Map<Class, JAXBContext> jaxbContextHashMap = new ConcurrentHashMap<Class, JAXBContext>();

    public void marshaller(Object object, OutputStream outputStream) {
        try {
            Marshaller m = buildMarshaller(object);
            m.marshal(object, outputStream);
        } catch (JAXBException e) {
            throw new RopException(e);
        }
    }


    public Marshaller buildMarshaller(Object object) throws JAXBException {
        if (!jaxbContextHashMap.containsKey(object.getClass())) {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            jaxbContextHashMap.put(object.getClass(), context);
        }
        JAXBContext context = jaxbContextHashMap.get(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        return marshaller;
    }
}

