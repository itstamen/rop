/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-6
 */
package com.rop.marshaller;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-3-27
 */
public class JacksonJsonRopMarshallerTest {

    private JacksonJsonRopMarshaller marshaller = new JacksonJsonRopMarshaller();

    @Test
    public void test1(){
        Foo foo = new Foo();
        foo.setB1(true);
        foo.setB2(true);
        foo.setI1(1);
        foo.setI2(1);
        Bar bar = new Bar();
        bar.setFoo(foo);
        marshaller.marshaller(bar,System.out);
    }

    @Test
    public void test2(){
        List<Foo> lists = Lists.newArrayList();
        Foo f1 = new Foo();
        f1.setI2(2);
        lists.add(f1);
        Foo f2 = new Foo();
        f2.setI2(1);
        lists.add(f2);
        marshaller.marshaller(lists,System.out);
        System.out.println("ddd");

        Map<String,Foo> maps= Maps.newLinkedHashMap();
        maps.put("1",f2);
        maps.put("2",f1);
        marshaller.marshaller(maps,System.out);
        System.out.println("aaa");
    }


}
