package com.greekk;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MyReflectionUtilz {


    public static Map<String,String> makeConfig(String path){
        Map<String, String> config = null;
        try{
            config = FileOperations.readConfig(path);
        }
        catch (IOException iox){
            iox.printStackTrace();
        }
        return config;
    }

    public static Class<?> getClazz( Map<String,String> config){
/*
        Class<?> clazz, List<Field> fields, List<Method> setters,
                List<Method> getters, Constructor constructor, Object obj
*/
        Class<?> clazz = null;
        try {
            clazz = Class.forName(config.get("class"));
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
//
//        catch(InstantiationException iex){
//            System.out.println("Instance was not created!");
//            return;
//        }
//
    }

    public static Constructor<?> makeConstructor(Class<?> clazz, Class<?>... args){
        Constructor<?> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor(args);
        }
        catch(NoSuchMethodException nsmex){
            nsmex.printStackTrace();
        }
        return constructor;
    }

    public static Object makeObject(Constructor<?> constr, ){
        Constructor<?> constructor = null;
        try{
            constructor = constr.newInstance("middle");
        }
        catch (IllegalAccessException iaex){
            iaex.printStackTrace();
        }
        catch (InvocationTargetException itex){
            itex.printStackTrace();
        }
        return constructor;
    }

    public static void fillObject(Map<String, String> config, List<Field> fields,
                                  List<Method> setters, List<Method> getters,Object obj){
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String fieldName = entry.getKey();
            if(fieldName == "class")
                continue;
            String value = entry.getValue();
            try{
                setValueToField(fieldName, fields, value, setters, getters, obj);
            }
            catch(Exception ex){
                ex.printStackTrace();
                return;
            }
        }
    }
    //takes all fields from the class
    public static List<Field> getFields(Class<?> clazz){
        List<Field> fields = new ArrayList();
        fields.addAll(List.of(clazz.getDeclaredFields()));
        fields.addAll(List.of(clazz.getSuperclass().getDeclaredFields()));
        return fields;
    }

    //takes all methods from the class
    public static List<Method> getMethods(Class<?> clazz){
        List<Method> methods = new ArrayList<>();
        methods.addAll(List.of(clazz.getDeclaredMethods()));
        methods.addAll(List.of(clazz.getSuperclass().getDeclaredMethods()));
        return methods;
    }

    //makes filtering of methods by the giving filter
    public static List<Method> getFilteredMethods( String filter, Class<?> clazz){
        List<Method> methods = getMethods(clazz);
        return methods.stream().
                filter(setter -> setter.getName().contains(filter))
                .collect(Collectors.toList());
    }

    //make camel-style method name for further calling
    private static String makeMethodName(String verb, String fieldName ){
        return verb + capitalizeFieldName(fieldName);
    }

    //check whether field have value
    private static boolean checkFieldOnValue(String getterName, List<Method> getters, Object obj)
            throws InvocationTargetException, IllegalAccessException
    {
        Method getter = findMethod(getters, getterName);
        Object result = getter.invoke(obj);//big trouble!!!!
            if(Objects.nonNull(result)){
                try{
                    if((float)result == 0f)
                        return false;
                    if((long)result == 0L)
                        return false;
                    if((double)result == 0d)
                        return false;
                }
                catch(Exception e){
                    return true;
                }
        }
        return false;
    }

    private static Method findMethod(List<Method> methods, String methodName){
        for (Method method : methods) {
            if(method.getName().equals(methodName)){
                return method;
            }
        }
        return null;
    }

    private static void setValueToField( String fieldName, List<Field> fields, String value, List<Method> setters, List<Method> getters, Object obj) throws Exception {
        Object innerValue = value;
        String setterName = makeMethodName("set" , fieldName);
        String getterName = makeMethodName("get" , fieldName);
        Method setter = findMethod(setters, setterName);
        Field field = findField(fieldName, fields);

        if(Objects.nonNull(field)){
            if(checkFieldOnValue(getterName, getters, obj))
                return;
            Class<?> fieldType = field.getType();
            if(fieldType == int.class)
                innerValue = Integer.parseInt(value);
            if(fieldType == float.class)
                innerValue = Float.parseFloat(value);
            if(fieldType == double.class)
                innerValue = Double.parseDouble(value);
            if(fieldType == long.class)
                innerValue = Long.parseLong(value);
            if(fieldType == UUID.class){
                innerValue = UUID.nameUUIDFromBytes(value.getBytes());
            }
            if (!field.isAccessible() && Objects.nonNull(setter)){
                setter.invoke(obj, innerValue);
            }
            else
                field.set(obj, value);
        }
    }

    public static Field findField(String fieldName, List<Field> fields){
        for (Field field : fields) {
            if(field.getName().equals(fieldName))
                return field;
        }
        return null;
    }

    private static String capitalizeFieldName(String fieldName){
        return fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }
}
