/*
 * Copyright 2013 Balazs Berkes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mockannotations.utils;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import static org.mockannotations.utils.MockAnnotationValidationUtils.notNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for reflection based operations.
 * <p>
 * @author Balazs Berkes
 */
public final class MockAnnotationReflectionUtils {

    /**
     * Set the field of the target object with the given value.
     * <p>
     * @param field field to set
     * @param target object with field to set
     * @param value value which will be used for the field
     * <p>
     * @throws UnableToWriteFieldException when setting the field failed
     */
    public static void setField(Field field, Object target, Object value) throws UnableToWriteFieldException {
        if (value != null) {
            if (!isStatic(field.getModifiers()) && !isFinal(field.getModifiers())) {
                doSetField(field, target, value);
            }
        }
    }

    private static void doSetField(Field field, Object target, Object value) throws UnableToWriteFieldException, SecurityException {
        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (Exception ex) {
            throw new UnableToWriteFieldException(field, ex);
        }
    }

    /**
     * Get the value of the field in the given object.
     * <p>
     * @param field which value will be returned
     * @param target the target object which contains the field
     * @return the value of the field
     * <p>
     * @throws UnableToReadFieldException when setting the field failed
     */
    public static Object getField(Field field, Object target) throws UnableToReadFieldException {
        Object fieldValue = null;
        if (notNull(target)) {
            fieldValue = doGetField(field, target);
        }
        return fieldValue;
    }

    private static Object doGetField(Field field, Object target) throws UnableToReadFieldException, SecurityException {
        field.setAccessible(true);
        try {
            return field.get(target);
        } catch (Exception ex) {
            throw new UnableToReadFieldException(field, ex);
        }
    }

    /**
     * Determines the distance of the given object to the given class in the
     * inheritance tree.
     * <p>
     * @param object the root object
     * @param clazz the class to determine the distance to
     * @return the distance of the object to the class. If the object is not an
     * instance of the class {@code -1} will return.
     */
    public static int getInheritanceDistance(Object object, Class<?> clazz) {
        if (clazz.isInstance(object)) {
            return calculateDistance(object.getClass(), clazz);
        } else {
            return -1;
        }
    }

    private static int calculateDistance(Class<?> compared, Class<?> clazz) {
        int distance = 0;
        while (compared != clazz) {
            compared = compared.getSuperclass();
            distance++;
            if (compared == Object.class) {
                break;
            }
        }
        return distance;
    }

    /**
     * Scans the class and all its predecessors (up to {@code Object}) for fields.
     * <p>
     * @param clazz first level class to scan
     * @return {@code List<Field>} of the class and all it predecessors
     */
    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> predecessor = clazz;

        while (!predecessor.equals(Object.class)) {
            fields.addAll(Arrays.asList(predecessor.getDeclaredFields()));
            predecessor = predecessor.getSuperclass();
        }

        return fields;
    }

    /**
     * Scans the class for its setter methods. Setter method must be public and non-static.
     * <p>
     * @param clazz the class to scan
     * @return {@code List<Method>} of the class which contains its setters
     */
    public static List<Method> getAllSetters(Class<?> clazz) {
        List<Method> setters = new ArrayList<Method>();

        for (Method method : clazz.getMethods()) {
            if (isPublic(method.getModifiers()) && isSetter(method)) {
                setters.add(method);
            }
        }

        return setters;
    }

    private static boolean isSetter(Method m) {
        return m.getName().startsWith("set");
    }

    /**
     * Returns the generic parameters of the given field.
     * <p>
     * @param field field to be scanned
     * @return the generic parameters
     */
    public static List<Type> getGenericParameters(Field field) {
        List<Type> genericParameters = Collections.emptyList();
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            genericParameters = new ArrayList<Type>();
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            genericParameters.addAll(Arrays.asList(parameterizedType.getActualTypeArguments()));
        }

        return genericParameters;
    }

    public static class UnableToWriteFieldException extends RuntimeException {

        public UnableToWriteFieldException(Field field, Throwable cause) {
            super(String.format("Cannot set value of field '%s'", field), cause);
        }
    }

    public static class UnableToReadFieldException extends RuntimeException {

        public UnableToReadFieldException(Field field, Throwable cause) {
            super(String.format("Cannot get value of field '%s'", field), cause);
        }
    }

    private MockAnnotationReflectionUtils() {
    }
}
