package com.vip.harry.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: harry.chen
 * @since: 14-4-9 下午3:54
 */
public class XmlUtils {

    private static final String SET_METHOD = "set";

    private static List<Class> typeList = new ArrayList<Class>();

    static {
        typeList.add(Integer.class);
        typeList.add(String.class);
        typeList.add(Boolean.class);
        typeList.add(Double.class);
        typeList.add(Long.class);
        typeList.add(Short.class);
        typeList.add(Float.class);
        typeList.add(Byte.class);
    }

    public static <T> T parseRemoteXml(String xml, Class<T> clazz) {
        Document document = null;

        T o = null;
        try {
            document = DocumentHelper.parseText(xml);
            Field[] fields = clazz.getDeclaredFields();
            Element rootElement = document.getRootElement();
            o = clazz.newInstance();

            //get superclass field
            Class superClazz = clazz.getSuperclass();
            while (superClazz != java.lang.Object.class) {
                Field[] superFields = superClazz.getFields();
                for (Field ff : superFields) {
                    o = (T) addBeanAttr(o, superClazz, rootElement, ff);
                }
                superClazz = superClazz.getSuperclass();
            }


            for (Field field : fields) {
                Object k = null;
                String attr = field.getName();
                PayParamAlias payParamAlias = field.getAnnotation(PayParamAlias.class);
                if (payParamAlias != null) {
                    attr = payParamAlias.value();
                }

                //获取复杂子类的参数值  todo.子类里面套子类功能未实现
                if (!typeList.contains(field.getType())) {//!String.class.equals(field.getType())
                    Class cls = field.getType();
                    k = cls.newInstance();
                    Field[] f = cls.getDeclaredFields();

                    for (Field subfield : f) {
                        k = addBeanAttr(k, cls, rootElement, subfield);
                    }
                } else {
                    //获取简单子类的参数值，如果element不包含这个attr，返回null
                    k = rootElement.elementText(attr);
                }

                Method m = clazz.getDeclaredMethod(getSetMethodName(field.getName()), field.getType());
                m.invoke(o, parseString2T(k.toString(), field.getType()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //LOG.error("parse remote response {} to class {} error, error: {} , cost {}ms", xml, clazz.getName(), e.getMessage(), System.currentTimeMillis() - start);
        }
        return o;
    }

    private static <T> T addBeanAttr(T t, Class<T> clazz, Element element, Field f) {
        String xmlAttr = f.getName();
        PayParamAlias payParamAlias = f.getAnnotation(PayParamAlias.class);
        if (payParamAlias != null) {
            xmlAttr = payParamAlias.value();
        }
        try {
            Method m = clazz.getDeclaredMethod(getSetMethodName(f.getName()), f.getType());
            m.invoke(t, parseString2T(element.elementText(xmlAttr), f.getType()));
        } catch (Exception e) {
            //LOG.error("Class {} has no attribute {}, error {}", clazz.getName(), f.getName(), e.getMessage());
        }
        return t;
    }

    public static String getSetMethodName(String attr) {
        return SET_METHOD + attr.substring(0, 1).toUpperCase() + attr.substring(1);
    }

    private static <T> Object parseString2T(String source, Class<T> clazz) {
        if (clazz.equals(Integer.class)) {
            return Integer.parseInt(source);
        } else if (clazz.equals(Boolean.class)) {
            if (source.equals("false")) {
                return false;
            } else {
                return true;
            }
        }
        return source;
    }

//    public static void main(String[] args) {
//        System.out.println(parseString2T("false", Boolean.class));
//    }
}
