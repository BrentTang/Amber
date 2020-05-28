package com.tzh.amber.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author 豪
 * @title: ProxyFactory
 * @projectName Java
 * @description: TODO
 * @date 2019/6/29 13:03
 */
public class ProxyFactory {

    public static <T> T createProxy(Class<T> interfaceClass, InvocationHandler invocationHandler) {

        if (interfaceClass == null) {
            return null;
        }

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, invocationHandler);
    }

}
