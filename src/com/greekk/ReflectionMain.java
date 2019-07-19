package com.greekk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.greekk.MyReflectionUtilz.*;

public class ReflectionMain {

    public static void main(String[] args) throws Exception {


        String configPath = "config.txt";
        Map<String, String> config = makeConfig(configPath);
        Class<?> clazz = getClazz(config);
        List<Field> fields = getFields(clazz);;
        List<Method> setters = getFilteredMethods("set", clazz);
        List<Method> getters = getFilteredMethods("get", clazz);
        Constructor<?> constructor = makeConstructor(String.class);
        Manager manager = ;

        ;
        //manager = fillObject(manager);
    }
}
