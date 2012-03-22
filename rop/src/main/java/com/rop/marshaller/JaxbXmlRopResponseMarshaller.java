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
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JaxbXmlRopResponseMarshaller implements RopResponseMarshaller {

    private static Map<Class, Marshaller> marshallerMap = new HashMap<Class, Marshaller>();

    public void marshaller(RopResponse response, OutputStream outputStream) {
        try {
            Marshaller m = getMarshaller(response);
            m.marshal(response, outputStream);
        } catch (JAXBException e) {
            throw new RopException(e);
        }
    }

    private Marshaller getMarshaller(RopResponse response) throws JAXBException {
        if (!marshallerMap.containsKey(response.getClass())) {
            JAXBContext context = JAXBContext.newInstance(response.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            marshallerMap.put(response.getClass(), marshaller);
        }
        return marshallerMap.get(response.getClass());
    }
}

