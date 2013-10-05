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

import static org.mockannotations.utils.MockAnnotationReflectionUtils.getAllDeclaredFields;
import static org.mockannotations.utils.MockAnnotationValidationUtils.notNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class which scans the fields for the annotation passed as generic parameter.
 * <p>
 * @param <A> type of the annotation
 * <p>
 * @author Balazs Berkes
 */
public class AnnotatedFieldScanner<A extends Annotation> {

    private final static Map<Class<? extends Annotation>, AnnotatedFieldScanner<?>> scanners
            = new HashMap<Class<? extends Annotation>, AnnotatedFieldScanner<?>>();

    private final Class<A> annotation;

    /**
     * Provides a singleton instance of {@code AnnotatedFieldScanner} of {@code T}.
     * <p>
     * @param <T> type of annotation
     * @param annotation class of the scanned annotation.
     * @return the scanner for desired annotation
     */
    public static synchronized <T extends Annotation> AnnotatedFieldScanner<T> getScanner(Class<T> annotation) {
        AnnotatedFieldScanner<T> scanner;
        if (scanners.containsKey(annotation)) {
            scanner = (AnnotatedFieldScanner<T>) scanners.get(annotation);
        } else {
            scanner = new AnnotatedFieldScanner<T>(annotation);
            scanners.put(annotation, scanner);
        }
        return scanner;
    }

    /**
     * Scans the fields of a {@code Object} for a specific annotation given as generic parameter.
     * <p>
     * @param target object to scan
     * @return list of fields which annotated with the scanner's annotation
     */
    public List<Field> scan(Object target) {
        if (notNull(target)) {
            return scan(target.getClass());
        } else {
            return Collections.emptyList();
        }

    }

    /**
     * Scans the fields of a {@code Class} for a specific annotation given as generic parameter.
     * <p>
     * @param clazz class to scan
     * @return list of fields which annotated with the scanner's annotation
     */
    public List<Field> scan(Class<?> clazz) {
        List<Field> annotatedFields = new LinkedList<Field>();

        for (Field field : getAllDeclaredFields(clazz)) {
            if (field.isAnnotationPresent(annotation)) {
                annotatedFields.add(field);
            }
        }

        return annotatedFields;
    }

    AnnotatedFieldScanner(Class<A> annotation) {
        this.annotation = annotation;
    }
}
