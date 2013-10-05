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

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link MockHolder}.
 * <p>
 * @author Balazs Berkes
 */
public class MockHolderTest {

    private static final String MOCK_NAME = "MOCK_NAME";
    private static final Object MOCK = new Object();

    private Field sourceField;

    @Before
    public void setUp() {
        initializeField();
    }

    @Test
    public void testEmptyMockShouldBeSingleton() {
        MockHolder mockHolder1 = MockHolder.emptyMock();
        MockHolder mockHolder2 = MockHolder.emptyMock();

        assertEquals(mockHolder1, mockHolder2);
    }

    @Test
    public void testCreate() {
        MockHolder created = MockHolder.create(MOCK, sourceField, MOCK_NAME);

        assertEquals(sourceField, created.getSourceField());
        assertEquals(MOCK, created.getMock());
        assertEquals(MOCK_NAME, created.getName());
    }

    @Test
    public void testGetSourceNameShouldReturnTheSourceFieldNameWhenSoruceIsNotNull() {
        MockHolder underTest = MockHolder.create(MOCK, sourceField, MOCK_NAME);

        assertEquals(sourceField.getName(), underTest.getSourceName());
    }

    @Test
    public void testGetSourceNameShouldReturnEmptyStringWhenSoruceIsNull() {
        MockHolder underTest = MockHolder.create(MOCK, null, MOCK_NAME);

        assertEquals("", underTest.getSourceName());
    }

    private void initializeField() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getName().equals("MOCK")) {
                field.setAccessible(true);
                sourceField = field;
                break;
            }
        }
    }
}
