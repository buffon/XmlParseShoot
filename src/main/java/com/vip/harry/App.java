package com.vip.harry;

import com.vip.harry.util.ShootBean;
import com.vip.harry.util.XmlUtils;

import java.lang.reflect.Field;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
//        String shootBean = "<ShootBean><name>chen</name><age>123</age><sex>man</sex><base>ba</base></ShootBean>";
//
//        ShootBean bean = XmlUtils.parseRemoteXml(shootBean, ShootBean.class);
//
//        System.out.println("age " + bean.getAge());
//        System.out.println("name " + bean.getName());
//        System.out.println("subbase sex " + bean.getSex());
//        System.out.println("base " + bean.getBase());

        Field[] fields = ShootBean.class.getFields();
        for (Field field : fields) {
            System.out.println(field.getName() + "  " + field.getType());
        }

//        System.out.println(Integer.class.( "123"));
    }
}
