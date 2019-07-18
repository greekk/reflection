package com.greekk;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MyReflectionUtilz {


    public static void init(String configFile, Map<?,?> config, Class<?> clazz, List<Field> fields, List<Method> setters,
                            List<Method> getters, Constructor constructor, ){
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
                continue;
            String value = entry.getValue();
            try{
                setValueToField(fieldName, value);
            }
            catch(Exception ex){
                ex.printStackTrace();
                return;
            }
        }
    }
    //takes all fields from the class
    private static List<Field> getFields(){
        List<Field> fields = new ArrayList();
        fields.addAll(List.of(clazz.getDeclaredFields()));
        fields.addAll(List.of(clazz.getSuperclass().getDeclaredFields()));
        return fields;
    }

    //takes all methods from the class
    private static List<Method> getMethods(){
        List<Method> methods = new ArrayList<>();
        methods.addAll(List.of(clazz.getDeclaredMethods()));
        methods.addAll(List.of(clazz.getSuperclass().getDeclaredMethods()));
        return methods;
    }

    //makes filtering of methods by the giving filter
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
    private static boolean checkFieldOnValue(String getterName) throws InvocationTargetException, IllegalAccessException {
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

    private static void setValueToField( String fieldName, String value) throws Exception {
        Object innerValue = value;
        String setterName = makeMethodName("set" , fieldName);
        String getterName = makeMethodName("get" , fieldName);
        Method setter = findMethod(setters, setterName);
        Field field = findField(fieldName);

        if(Objects.nonNull(field)){
            if(checkFieldOnValue(getterName))
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
