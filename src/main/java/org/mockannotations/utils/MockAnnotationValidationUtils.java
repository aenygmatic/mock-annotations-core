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

/**
 * Utility class for validation based operations.
 * <p>
 * @author Balazs Berkes
 */
public final class MockAnnotationValidationUtils {

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean notEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    public static boolean notNull(Object object) {
        return object != null;
    }

    public static void assertNotNull(Object object, String message) throws IllegalArgumentException {
        if (isNull(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    private MockAnnotationValidationUtils() {
    }
}
