package com.lambert.hrpc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;

/**
 * Created by pc on 2015/12/16.
 */
public class RpcBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static Logger LOGGER = LoggerFactory.getLogger(RpcBeanDefinitionParser.class);

    private Class<?> defaultBeanClass ;

    @Override
    protected Class<?> getBeanClass(Element element) {

        String value = element.getAttribute("class");
        if(value != null) {
            try {
                defaultBeanClass = ClassUtils.forName(value.trim(), getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                LOGGER.error("class {} not found "  ,value , e);
            }
        }
        // return default bean class;
        return this.defaultBeanClass;
    }

    public RpcBeanDefinitionParser(Class<?> beanClass) {
        this.defaultBeanClass = beanClass;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        NamedNodeMap attributes =  element.getAttributes();
        for(int i = 0 , len = attributes.getLength() ; i < len ; i ++) {

            Node node = attributes.item(i);
            String name = node.getLocalName();
            String value = node.getNodeValue();

            if(value != null){
                value = value.trim();
                if(value.length() > 0) {

                    try {
                        Field field = defaultBeanClass.getField(name);
                        if(field.getType().getName().startsWith("com.lambert.hrpc")){
                            builder.getBeanDefinition().getPropertyValues().addPropertyValue(name , new RuntimeBeanReference(value));
                        } else {
                            builder.getBeanDefinition().getPropertyValues().addPropertyValue(name , value);
                        }
                    } catch (NoSuchFieldException e) {
                        LOGGER.error("no this {} file in {}"  , e);
                    }
                }
            }
        }
        // 解析子标签
        parseProperties(element.getChildNodes(),builder.getBeanDefinition());
    }

    private  void parseProperties(NodeList nodeList, BeanDefinition beanDefinition) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("property".equals(node.getNodeName()) || "property".equals(node.getLocalName())) {
                        String name = ((Element) node).getAttribute("name");
                        if (name != null && name.length() > 0) {
                            String value = ((Element) node).getAttribute("value");
                            String ref = ((Element) node).getAttribute("ref");
                            if (value != null && value.length() > 0) {
                                beanDefinition.getPropertyValues().addPropertyValue(name, value);
                            } else if (ref != null && ref.length() > 0) {
                                beanDefinition.getPropertyValues().addPropertyValue(name, new RuntimeBeanReference(ref));
                            } else {
                                throw new UnsupportedOperationException("Unsupported <property name=\"" + name + "\"> sub tag, Only supported <property name=\"" + name + "\" ref=\"...\" /> or <property name=\"" + name + "\" value=\"...\" />");
                            }
                        }
                    }
                }
            }
        }
    }

}
