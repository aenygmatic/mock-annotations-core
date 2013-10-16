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
package org.mockannotations;

import static org.mockannotations.utils.MockAnnotationValidationUtils.isNull;
import static org.mockannotations.utils.MockAnnotationValidationUtils.notNull;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.mockannotations.selection.ByTypeSelector;
import org.mockannotations.selection.MockSelector;

/**
 * Creates a new instance of the given class.
 *
 * @author Balazs Berkes
 */
public class ClassInitializer {

    /**
     * Initialize an instance of the given class. If the class has multiple
     * constructors the constructor with the less parameter will be prefered,
     * however if the initialization fails more parametrized constructor will be
     * the next.
     * <p>
     * @param clazz class to be initialized
     * @param mocks list of mocked object which can be used as constructor
     * parameter
     * @return a new instance of the given class.
     * <p>
     * @throws InitializationException when initialization failed
     */
    public Object initialize(Class<?> clazz, List<MockHolder> mocks) throws InitializationException {
        return new Initializer(clazz).withParameters(mocks).initialize();
    }

    public static class InitializationException extends RuntimeException {

        public InitializationException(Class<?> clazz) {
            super(String.format("I tried to create an instance of %s but it failed. Please provide an instance of the "
                    + "class before initializing the annotations.", clazz));
        }
    }

    private static class Initializer {

        private static final ConstructorComparator CONSTRUCTOR_COMPARATOR = new ConstructorComparator();

        private MockSelector<Class<?>> byTypeSelector = ByTypeSelector.getSingleton();
        private List<MockHolder> mocks = Collections.emptyList();
        private List<Constructor<?>> constructors;
        private Class<?> clazz;

        private Initializer(Class<?> clazz) {
            this.clazz = clazz;
            constructors = Arrays.asList(clazz.getDeclaredConstructors());
            Collections.sort(constructors, CONSTRUCTOR_COMPARATOR);
        }

        private Initializer withParameters(List<MockHolder> parameters) {
            mocks = parameters;
            return this;
        }

        private Object initialize() {
            Object objectToInitialize = initializeWithDefaultConstructor();
            if (isNull(objectToInitialize)) {
                objectToInitialize = initializeWithParameters();
            }
            if (isNull(objectToInitialize)) {
                throw new InitializationException(clazz);
            }
            return objectToInitialize;
        }

        private Object initializeWithDefaultConstructor() {
            Constructor<?> defaultConstructor = findDefaultConstructor();
            return exceptionFreeNewInstance(defaultConstructor);
        }

        private Constructor<?> findDefaultConstructor() {
            Constructor<?> defaultConstructor = null;
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterTypes().length == 0) {
                    defaultConstructor = constructor;
                    break;
                }
            }
            return defaultConstructor;
        }

        private Object initializeWithParameters() {
            Object testedObject = null;
            for (Constructor<?> constructor : constructors) {
                testedObject = tryToCreateInstance(constructor);
                if (notNull(testedObject)) {
                    break;
                }
            }
            return testedObject;
        }

        private Object tryToCreateInstance(Constructor<?> constructor) {
            constructor.setAccessible(true);
            Object instance = null;
            List<Object> parameterCandidates = selectParameterCandidates(constructor);
            if (!parameterCandidates.isEmpty()) {
                instance = exceptionFreeNewInstance(constructor, parameterCandidates.toArray());
            }
            return instance;
        }

        private List<Object> selectParameterCandidates(Constructor<?> constructor) {
            List<Object> parameterMocks = new LinkedList<Object>();
            for (Class<?> parameterType : constructor.getParameterTypes()) {
                List<MockHolder> matchingMocks = byTypeSelector.select(parameterType, mocks);
                if (matchingMocks.isEmpty()) {
                    break;
                }
                parameterMocks.add(matchingMocks.get(0).getMock());
            }
            return parameterMocks;
        }

        private Object exceptionFreeNewInstance(Constructor<?> constructor, Object... arguments) {
            Object newInstance = null;
            try {
                newInstance = constructor.newInstance(arguments);
            } catch (Exception ignored) {
                /* Ignoring any exception */
            }
            return newInstance;
        }
    }

    private static class ConstructorComparator implements Comparator<Constructor<?>> {

        public int compare(Constructor<?> left, Constructor<?> right) {
            return left.getParameterTypes().length - right.getParameterTypes().length;
        }
    }
}
