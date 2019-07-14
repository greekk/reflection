package com.greekk;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyReflectionUtilz {

    private static Map<String, String> config;
    public static Class<?> clazz;
    private static List<Field> fields;
    private static List<Method> setters;
    private static List<Method> getters;
    private static Constructor constructor;
    private static Object obj;


    public static void init(String configFile){
        try {
            config = FileOperations.readConfig(configFile);
            clazz = Class.forName(config.get("class"));
            fields = getFields();
            setters = getFilteredMethods("set");
            getters = getFilteredMethods("get");
            constructor = clazz.getDeclaredConstructor(String.class);

            try{
                obj = constructor.newInstance("middle");
            }
            catch(Exception ex){
                System.out.println("Instance was not created!");
                return;
            }
            fillObject();

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException iox){
            System.out.println("File not found!");
        }
        catch(NoSuchMethodException nsmex){
            System.out.println("No such method!");
        }
    }

    private static void fillObject(){
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String fieldName = entry.getKey();
            if(fieldName == "class")
                return;
            String value = entry.getValue();
            try{
                setValueToField(fieldName, value);
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
                return;
            }
        }
    }

    private static List<Field> getFields(){
        List<Field> fields = new ArrayList();
        fields.addAll(List.of(clazz.getDeclaredFields()));
        fields.addAll(List.of(clazz.getSuperclass().getDeclaredFields()));
        return fields;
    }

    private static List<Method> getMethods(){
        List<Method> methods = new ArrayList<>();
        methods.addAll(List.of(clazz.getDeclaredMethods()));
        methods.addAll(List.of(clazz.getSuperclass().getDeclaredMethods()));
        return methods;
    }

    public static List<Method> getFilteredMethods( String filter){
        List<Method> methods = getMethods();
        return methods.stream().
                filter(setter -> setter.getName().contains(filter))
                .collect(Collectors.toList());
    }

    //make camel-style method name for further calling
    private static String makeMethodName(String verb, String fieldName ){
        return verb + capitalizeFieldName(fieldName);
    }
    //check whether field have value
    private static boolean checkFieldValue(String getterName) throws InvocationTargetException, IllegalAccessException {
        Method getter = findMethod(getters, getterName);
        Object result = getter.invoke(obj);
        if(Objects.nonNull(result))
            return true;
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

    private static void setValueToField( String fieldName, String value) throws Exception {
        String setterName = makeMethodName("set" , fieldName);
        String getterName = makeMethodName("get" , fieldName);

        if(checkFieldValue(getterName))
            return;
        Field field = findField(fieldName);

        if(Objects.nonNull(field)){
            Object fieldType = field.getType();
            if (!field.isAccessible()){
                Method setter = findMethod(setters, setterName);
                if(Objects.nonNull(setter)){
                    if(fieldType == float.class){
                        Float val = Float.parseFloat(value);
                        setter.invoke(obj, val);
                    }
                    else
                        setter.invoke(obj, value);
                }
            }
            else
                field.set(obj, value);
        }
    }

    public static Field findField(String fieldName){
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
