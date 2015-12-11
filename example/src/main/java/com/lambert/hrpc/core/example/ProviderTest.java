package com.lambert.hrpc.core.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by pc on 2015/12/9.
 */
public class ProviderTest {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("provider.xml");
    }
}
