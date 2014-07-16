/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-8
 */
package com.rop.sample.converter;

import com.rop.request.RopConverter;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stamen
 * Date: 13-10-25
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
public class CollectionsConverter implements RopConverter<String, Collection> {


    public String unconvert(Collection target) {
        return null;
    }


    public Class<String> getSourceClass() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Class<Collection> getTargetClass() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Collection convert(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
