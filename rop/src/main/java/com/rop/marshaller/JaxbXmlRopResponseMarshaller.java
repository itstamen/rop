/**
 *
 * 日    期：12-2-27
 */
package com.rop.marshaller;

import com.rop.RopException;
import com.rop.RopResponse;
import com.rop.RopResponseMarshaller;

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
public class JaxbXmlRopResponseMarshaller implements RopResponseMarshaller {

    private static Map<Class, JAXBContext> jaxbContextHashMap = new ConcurrentHashMap<Class, JAXBContext>();

    public void marshaller(RopResponse response, OutputStream outputStream) {
        try {
            Marshaller m = buildMarshaller(response);
            m.marshal(response, outputStream);
        } catch (JAXBException e) {
            throw new RopException(e);
        }
    }


    public Marshaller buildMarshaller(RopResponse response) throws JAXBException {
        if (!jaxbContextHashMap.containsKey(response.getClass())) {
            JAXBContext context = JAXBContext.newInstance(response.getClass());
            jaxbContextHashMap.put(response.getClass(), context);
        }
        JAXBContext context = jaxbContextHashMap.get(response.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        return marshaller;
    }
}

