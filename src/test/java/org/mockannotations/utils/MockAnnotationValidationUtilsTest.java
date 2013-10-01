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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for {@link MockAnnotationValidationUtils}.
 * <p>
 * @author Balazs Berkes
 */
public class MockAnnotationValidationUtilsTest {

    public static final String ASSERT_MESSAGE = "Assert message";

    @Test
    public void testIsNullWithNotNull() {
        assertFalse(MockAnnotationValidationUtils.isNull(new Object()));
    }

    @Test
    public void testIsNullWithNull() {
        assertTrue(MockAnnotationValidationUtils.isNull(null));
    }

    @Test
    public void testNotNullWithNotNull() {
        assertTrue(MockAnnotationValidationUtils.notNull(new Object()));
    }

    @Test
    public void testNotNullWithNull() {
        assertFalse(MockAnnotationValidationUtils.notNull(null));
    }

    @Test
    public void testIsEmptyWithEmptyString() {
        assertTrue(MockAnnotationValidationUtils.isEmpty(""));
    }

    @Test
    public void testIsEmptyWithNull() {
        assertTrue(MockAnnotationValidationUtils.isEmpty(null));
    }

    @Test
    public void testIsEmptyWithString() {
        assertFalse(MockAnnotationValidationUtils.isEmpty("String"));
    }

    @Test
    public void testAssertNotNullWithNotNull() {
        MockAnnotationValidationUtils.assertNotNull(new Object(), ASSERT_MESSAGE);
    }

    @Test
    public void testAssertNotNullWithNull() {
        try {
            MockAnnotationValidationUtils.assertNotNull(null, ASSERT_MESSAGE);
        } catch (IllegalArgumentException ex) {
            assertEquals(ASSERT_MESSAGE, ex.getMessage());
        }
    }
}
