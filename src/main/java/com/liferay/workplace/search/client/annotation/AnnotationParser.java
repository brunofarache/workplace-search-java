package com.liferay.workplace.search.client.annotation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.workplace.search.client.Parser;

import java.lang.reflect.Method;

public class AnnotationParser implements Parser {

    @Override
    public JsonObject toJsonObject(Object object) throws Exception {
        JsonObject jsonObject = new JsonObject();

        parseFields(object, jsonObject);
        parsePermissions(object, jsonObject);

        return jsonObject;
    }

    protected void parseFields(Object object, JsonObject jsonObject) throws Exception {
        java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();

        for (java.lang.reflect.Field field : fields) {
            _parseField(object, jsonObject, field);
        }

        Method[] methods = object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            _parseFieldMethod(object, jsonObject, method);
        }
    }

    protected void parsePermissions(Object object, JsonObject jsonObject) throws Exception {
        java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();

        for (java.lang.reflect.Field field : fields) {
            _parsePermissionsField(object, jsonObject, field);
        }

        Method[] methods = object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            _parsePermissionsMethod(object, jsonObject, method);
        }
    }

    private void _parseField(Object object, JsonObject jsonObject, java.lang.reflect.Field field) throws Exception {

        Field fieldAnnotation = field.getAnnotation(Field.class);

        if (fieldAnnotation != null) {
            field.setAccessible(true);

            String fieldName = fieldAnnotation.name();

            if (fieldName == null || fieldName.trim().isEmpty()) {
                fieldName = field.getName();
            }

            jsonObject.add(fieldName, _gson.toJsonTree(field.get(object)));
        }
    }

    private void _parsePermissionsField(Object object, JsonObject jsonObject, java.lang.reflect.Field field)
        throws Exception {

        field.setAccessible(true);

        AllowPermissions allowPermissions = field.getAnnotation(AllowPermissions.class);

        if (allowPermissions != null) {
            jsonObject.add(ALLOW_PERMISSIONS, _gson.toJsonTree(field.get(object)));
        }

        DenyPermissions denyPermissions = field.getAnnotation(DenyPermissions.class);

        if (denyPermissions != null) {
            jsonObject.add(DENY_PERMISSIONS, _gson.toJsonTree(field.get(object)));
        }
    }
    private void _parseFieldMethod(Object object, JsonObject jsonObject, Method method) throws Exception {
        method.setAccessible(true);

        Field fieldAnnotation = method.getAnnotation(Field.class);

        if (fieldAnnotation != null) {
            String fieldName = fieldAnnotation.name();

            if (fieldName == null || fieldName.trim().isEmpty()) {
                fieldName = method.getName();
            }

            jsonObject.add(fieldName, _gson.toJsonTree(method.invoke(object)));
        }
    }

    private void _parsePermissionsMethod(Object object, JsonObject jsonObject, Method method) throws Exception {
        method.setAccessible(true);

        AllowPermissions allowPermissions = method.getAnnotation(AllowPermissions.class);

        if (allowPermissions != null) {
            jsonObject.add(ALLOW_PERMISSIONS, _gson.toJsonTree(method.invoke(object)));
        }

        DenyPermissions denyPermissions = method.getAnnotation(DenyPermissions.class);

        if (denyPermissions != null) {
            jsonObject.add(DENY_PERMISSIONS, _gson.toJsonTree(method.invoke(object)));
        }
    }

    protected static final String ALLOW_PERMISSIONS = "_allow_permissions";
    protected static final String DENY_PERMISSIONS = "_deny_permissions";

    private Gson _gson = new Gson();

}