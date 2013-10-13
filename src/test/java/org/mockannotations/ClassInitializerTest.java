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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Before;
import org.junit.Test;

import org.mockannotations.ClassInitializer.InitializationException;

/**
 * Unit test for {@link ClassInitializer}.
 * <p>
 * @author Balazs Berkes
 */
public class ClassInitializerTest {

    private List<MockHolder> mocks;
    private Class<?> clazz;
    private String string = "";
    private Object object = new Object();

    private ClassInitializer underTest;

    @Before
    public void setUp() {
        underTest = new ClassInitializer();
    }

    @Test
    public void testInitializeShouldCreateNewInstanceWhenClassHasDefaultConstructor() {
        givenMocksToInject();
        givenClassToInitialize(ReentrantLock.class);

        Object actualClass = underTest.initialize(clazz, mocks);

        assertObjectInitialized(actualClass);
    }

    @Test
    public void testInitializeShouldCreateNewInstanceWhenClassHasConstructorWithOneParameter() {
        givenMocksToInject(string);
        givenClassToInitialize(StringContructor.class);

        Object actualClass = underTest.initialize(clazz, mocks);

        assertObjectInitialized(actualClass);
    }

    @Test
    public void testInitializeShouldCreateNewInstanceWhenClassHasConstructorWithMoreParameters() {
        givenMocksToInject(string, object);
        givenClassToInitialize(MultiParamConstructor.class);

        Object actualClass = underTest.initialize(clazz, mocks);

        assertObjectInitialized(actualClass);
    }

    @Test
    public void testInitializeShouldInjectParameterWhenClassHasConstructorWithMoreParameters() {
        givenMocksToInject(string, object);
        givenClassToInitialize(MultiParamConstructor.class);

        MultiParamConstructor actualClass = (MultiParamConstructor) underTest.initialize(clazz, mocks);

        assertEquals(string, actualClass.getString());
        assertEquals(object, actualClass.getObject());
    }

    @Test
    public void testInitializeShouldPreferDefaultConstructorWhenClassHasDefaultAndParameterized() {
        givenMocksToInject(string, object);
        givenClassToInitialize(MultiParamConstructorWithDefault.class);

        MultiParamConstructorWithDefault actualClass = (MultiParamConstructorWithDefault) underTest.initialize(clazz, mocks);

        assertNull(actualClass.getString());
        assertNull(actualClass.getObject());
    }

    @Test
    public void testInitializeShouldPreferLessParameterizedConstructorWhenClassHasMore() {
        givenMocksToInject(string, object);
        givenClassToInitialize(MultiParamConstructorWithMoreVersion.class);

        MultiParamConstructorWithMoreVersion actualClass = (MultiParamConstructorWithMoreVersion) underTest.initialize(clazz, mocks);

        assertEquals(string, actualClass.getString());
        assertNull(actualClass.getObject());
    }

    @Test(expected = InitializationException.class)
    public void testInitializeShouldThrowExceptionWhenClassCannotBeInstantiated() {
        givenClassToInitialize(ExceptionConstructor.class);

        underTest.initialize(clazz, mocks);
    }

    @Test(expected = InitializationException.class)
    public void testInitializeShouldThrowExceptionWhenNoConstructorCanBeFullyInjected() {
        givenMocksToInject(object);
        givenClassToInitialize(MultiParamConstructorWithMoreVersion.class);

        underTest.initialize(clazz, mocks);
    }

    private void givenClassToInitialize(Class<?> clazz) {
        this.clazz = clazz;
    }

    private void assertObjectInitialized(Object actualClass) {
        assertNotNull(actualClass);
        assertTrue(clazz.isInstance(actualClass));
    }

    private void givenMocksToInject(Object... mocks) {
        this.mocks = new LinkedList<MockHolder>();
        for (Object mock : mocks) {
            MockHolder mockHolder = new MockHolder();
            mockHolder.setMock(mock);
            this.mocks.add(mockHolder);
        }
    }

    public static class StringContructor {

        private String string;

        public StringContructor(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    public static class MultiParamConstructor {

        private String string;
        private Object object;

        public MultiParamConstructor(String string, Object object) {
            this.string = string;
            this.object = object;
        }

        public String getString() {
            return string;
        }

        public Object getObject() {
            return object;
        }
    }

    public static class MultiParamConstructorWithDefault {

        private String string;
        private Object object;

        public MultiParamConstructorWithDefault() {
        }

        public MultiParamConstructorWithDefault(String string, Object object) {
            this.string = string;
            this.object = object;
        }

        public String getString() {
            return string;
        }

        public Object getObject() {
            return object;
        }
    }

    public static class MultiParamConstructorWithMoreVersion {

        private String string;
        private Object object;

        public MultiParamConstructorWithMoreVersion(String string) {
            this.string = string;
        }

        public MultiParamConstructorWithMoreVersion(String string, Object object) {
            this.string = string;
            this.object = object;
        }

        public String getString() {
            return string;
        }

        public Object getObject() {
            return object;
        }
    }

    public static class ExceptionConstructor {

        public ExceptionConstructor() {
            throw new RuntimeException();
        }
    }
}
