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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockannotations.utils.MockAnnotationReflectionUtils.getAllDeclaredFields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link AnnotationScanner}.
 * <p>
 * @author Balazs Berkes
 */
public class AnnotationScannerTest {

    private Class<?> targetClass;

    private AnnotationScanner<Marked> underTest;

    @Before
    public void setUp() {
        underTest = new AnnotationScanner<Marked>(Marked.class);
    }

    @Test
    public void testGetScannerShouldBeSingleton() {
        AnnotationScanner<Marked> scanner1 = AnnotationScanner.getScanner(Marked.class);
        AnnotationScanner<Marked> scanner2 = AnnotationScanner.getScanner(Marked.class);

        assertEquals(scanner1, scanner2);
    }

    @Test
    public void testScanShouldFindAllAnnotatedField() {
        givenScannedClass(AnnotatedClass.class);

        List<Field> fields = underTest.scan(targetClass);

        assertFieldsFound(fields, List.class, Object.class);
    }

    @Test
    public void testScanShouldFindAllAnnotatedFieldInferitedIncluded() {
        givenScannedClass(AnnotatedSubClass.class);

        List<Field> fields = underTest.scan(targetClass);

        assertFieldsFound(fields, List.class, Object.class, Set.class);
    }

    private void givenScannedClass(Class<?> target) {
        targetClass = target;
    }

    private void assertFieldsFound(List<Field> fields, Class<?>... classes) {
        assertEquals(classes.length, fields.size());
        for (Class<?> c : classes) {
            assertTrue(fields.contains(getField(c, targetClass)));
        }
    }

    private Field getField(Class<?> fieldType, Class<?> targetClass) {
        Field field = null;
        for (Field f : getAllDeclaredFields(targetClass)) {
            if (f.getType() == fieldType) {
                field = f;
            }
        }
        return field;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Marked {
    }

    public class AnnotatedClass {

        @Marked
        List<?> list;
        @Marked
        Object object;

        String string;
        Thread thread;
    }

    public class AnnotatedSubClass extends AnnotatedClass {

        @Marked
        Set<?> set;
    }
}
