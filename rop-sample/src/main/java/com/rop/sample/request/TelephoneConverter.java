/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-7
 */
package com.rop.sample.request;

import com.rop.request.RopConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class TelephoneConverter implements RopConverter<String, Telephone> {

    @Override
    public Telephone convert(String source) {
        if (StringUtils.hasText(source)) {
            String zoneCode = source.substring(0, source.indexOf("-"));
            String telephoneCode = source.substring(source.indexOf("-") + 1);
            Telephone telephone = new Telephone();
            telephone.setZoneCode(zoneCode);
            telephone.setTelephoneCode(telephoneCode);
            return telephone;
        } else {
            return null;
        }
    }

    @Override
    public String unconvert(Telephone target) {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getZoneCode());
        sb.append("-");
        sb.append(target.getTelephoneCode());
        return sb.toString();
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Telephone> getTargetClass() {
        return Telephone.class;
    }
}

