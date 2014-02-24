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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link MockInjector}.
 *
 * @author Balazs Berkes
 */
public class MockInjectorTest {

    /* Fields to inject */
    private SuperClass superClass;
    private SuperClass anotherSuperClass;
    private Clazz clazz;
    private SubClass subClass;
    private HashSet<String> set;
    /* Objects to be injected */
    private TestedClassWithAllUniqueField classUniqueTypeField;
    private TestedClassWithFieldsOfSameType classUniqueNamedFields;
    private TestedClassWithFieldsOfSameTypeLowCaseOnly classUniqueLowCaseNamedFields;
    private TestedClassWithInterfaceField classWithInterfaceField;
    private TestedClassWithInheritedFields classWithInheritedFields;

    private List<MockHolder> mocks;

    private MockInjector underTest;

    @Before
    public void setUp() {
        initializeTestComponents();
    }

    @Test
    public void testInjectMocksWhenAllFieldHasUniqueType() {
        givenMocks(superClass, clazz, subClass);
        givenClassWithUniqueTypeFields();

        underTest.injectTo(classUniqueTypeField);

        assertFieldsAreInjectedByType();
    }

    @Test
    public void testInjectMocksWhenFieldsHaveTheSameTypeShouldMatchByName() {
        givenMocks(superClass, anotherSuperClass);
        givenClassWithUniqueNamedFieldsOfSameType();

        underTest.injectTo(classUniqueNamedFields);

        assertFieldsInjectedByName();
    }

    @Test
    public void testInjectMocksWhenFieldsHaveTheSameTypeShouldMatchByLowerCaseName() {
        givenMocks(superClass, anotherSuperClass);
        givenClassWithUniqueLowerCaseNamedFieldsOfSameType();

        underTest.injectTo(classUniqueLowCaseNamedFields);

        assertFieldsInjectedByLowerCaseName();
    }

    @Test
    public void testInjectMocksWhenFieldIsInterfaceShouldInjectImplementation() {
        givenMocks(set);
        givenTestedClassWithInterfaceField();

        underTest.injectTo(classWithInterfaceField);

        assertInterfaceImplIsInjected();

    }

    @Test
    public void testInjectMocksShouldInjectInheritedFields() {
        givenMocks(superClass, clazz, subClass);
        givenTestedClassWithInheritedFields();

        underTest.injectTo(classWithInheritedFields);

        assertInheritedFieldsAreInjected();

    }

    private void givenMocks(Object... mocks) {
        this.mocks = new ArrayList<MockHolder>();
        for (Object mock : mocks) {
            MockHolder m = new MockHolder();
            m.setMock(mock);
            m.setSourceField(getFieldNameOf(mock));
            this.mocks.add(m);
        }
        underTest = new MockInjector(this.mocks);
    }

    private void givenTestedClassWithInterfaceField() {
        classWithInterfaceField = new TestedClassWithInterfaceField();
    }

    private void givenTestedClassWithInheritedFields() {
        classWithInheritedFields = new TestedClassWithInheritedFields();
    }

    private void assertInheritedFieldsAreInjected() {
        assertEquals(superClass, classWithInheritedFields.superClass);
        assertEquals(clazz, classWithInheritedFields.clazz);
        assertEquals(subClass, classWithInheritedFields.subClass);
    }

    private void assertFieldsAreInjectedByType() {
        assertEquals(superClass, classUniqueTypeField.superClass);
        assertEquals(clazz, classUniqueTypeField.clazz);
        assertEquals(subClass, classUniqueTypeField.subClass);
    }

    private void givenClassWithUniqueTypeFields() {
        classUniqueTypeField = new TestedClassWithAllUniqueField();
    }

    private void givenClassWithUniqueNamedFieldsOfSameType() {
        classUniqueNamedFields = new TestedClassWithFieldsOfSameType();
    }

    private void givenClassWithUniqueLowerCaseNamedFieldsOfSameType() {
        classUniqueLowCaseNamedFields = new TestedClassWithFieldsOfSameTypeLowCaseOnly();
    }

    private void assertInterfaceImplIsInjected() {
        assertEquals(set, classWithInterfaceField.strings);
    }

    private void assertFieldsInjectedByName() {
        assertEquals(superClass, classUniqueNamedFields.superClass);
        assertEquals(anotherSuperClass, classUniqueNamedFields.anotherSuperClass);
    }

    private void assertFieldsInjectedByLowerCaseName() {
        assertEquals(superClass, classUniqueLowCaseNamedFields.superclass);
        assertEquals(anotherSuperClass, classUniqueLowCaseNamedFields.anothersuperclass);
    }

    private Field getFieldNameOf(Object mock) {
        Field field = null;
        for (Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (mock == f.get(this)) {
                    field = f;
                }
            } catch (Exception ex) {
            }
        }
        return field;
    }

    private void initializeTestComponents() {
        superClass = new SuperClass();
        anotherSuperClass = new SuperClass();
        clazz = new Clazz();
        subClass = new SubClass();
        set = new HashSet<String>();
    }

    private static class SuperClass {
    }

    private static class Clazz extends SuperClass {
    }

    private static class SubClass extends Clazz {
    }

    private static class TestedClassWithAllUniqueField {

        SuperClass superClass;
        Clazz clazz;
        SubClass subClass;
    }

    private static class TestedClassWithInheritedFields extends TestedClassWithAllUniqueField {
    }

    private static class TestedClassWithFieldsOfSameType {

        SuperClass superClass;
        SuperClass anotherSuperClass;
    }

    private static class TestedClassWithFieldsOfSameTypeLowCaseOnly {

        SuperClass superclass;
        SuperClass anothersuperclass;
    }

    private static class TestedClassWithInterfaceField {

        Set<String> strings;
    }
}
