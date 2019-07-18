package com.greekk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import static com.greekk.MyReflectionUtilz.*;

public class ReflectionMain {

    public static void main(String[] args) throws Exception {


        String configFile = "config.txt";
        Map<String, String> config;
        Class<?> clazz = n;
        List<Field> fields;
        List<Method> setters;
        List<Method> getters;
        Constructor<?> constructor;
        Object obj;

        MyReflectionUtilz.init(configFile, clazz, fields, );
    }
}
