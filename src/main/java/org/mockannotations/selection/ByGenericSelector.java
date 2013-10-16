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
package org.mockannotations.selection;

import static org.mockannotations.utils.MockAnnotationReflectionUtils.getGenericParameters;
import static org.mockannotations.utils.MockAnnotationValidationUtils.isNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.mockannotations.MockHolder;

/**
 * Selects the the mocks which has the same generic type.
 * <p>
 * @author Balazs Berkes
 */
public class ByGenericSelector implements MockSelector<Field> {

    private static MockSelector<Field> singleton;

    public static synchronized MockSelector<Field> getSingleton() {
        if (isNull(singleton)) {
            singleton = new ByGenericSelector();
        }
        return singleton;
    }

    @Override
    public List<MockHolder> selectByField(Field selection, List<MockHolder> mocks) {
        return select(selection, mocks);
    }

    @Override
    public List<MockHolder> select(Field targetField, List<MockHolder> mocks) {
        List<MockHolder> matchingMocks = new LinkedList<MockHolder>();
        List<Type> targetGenerics = getGenericParameters(targetField);
        for (MockHolder mockHolder : mocks) {
            List<Type> sourceGenerics = mockHolder.getGenericParameters();
            if (genericParametersAreMatching(targetGenerics, sourceGenerics)) {
                matchingMocks.add(mockHolder);
            }
        }
        return matchingMocks;
    }

    private boolean genericParametersAreMatching(List<Type> targetGenerics, List<Type> sourceGenerics) {
        return orderlyEquals(sourceGenerics, targetGenerics);
    }

    private boolean orderlyEquals(List<Type> left, List<Type> right) {
        boolean equals = false;
        if (left.size() == right.size()) {
            equals = true;
            for (int i = 0; i < right.size(); i++) {
                Type leftElement = left.get(i);
                Type rightElement = right.get(i);
                if (!leftElement.equals(rightElement)) {
                    equals = false;
                }
            }
        }
        return equals;
    }
}
