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

import static org.mockannotations.utils.MockAnnotationReflectionUtils.getAllDeclaredFields;
import static org.mockannotations.utils.MockAnnotationReflectionUtils.setField;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.mockannotations.selection.ByGenericSelector;
import org.mockannotations.selection.ByNameSelector;
import org.mockannotations.selection.ByTypeSelector;
import org.mockannotations.selection.MockSelector;

/**
 * Injects the the given mocks into the target class. Mocks are injected by type and name.
 *
 * @author Balazs Berkes
 */
public class MockInjector {

    private MockSelector<String> byNameSelector = ByNameSelector.getSingleton();
    private MockSelector<Class<?>> byTypeSelector = ByTypeSelector.getSingleton();
    private MockSelector<Field> byGenericSelector = ByGenericSelector.getSingleton();
    private List<MockSelector<?>> selectors = Arrays.asList(byTypeSelector, byGenericSelector, byNameSelector);
    private List<MockHolder> mocks;

    public MockInjector(List<MockHolder> mocks) {
        this.mocks = mocks;
    }

    /**
     * Injects the previously given mock into the target object.
     * <p>
     * @param target object to be injected
     * @return the target object
     */
    public Object injectTo(Object target) {
        for (Field field : getAllDeclaredFields(target.getClass())) {
            injectField(field, target);
        }
        return target;
    }

    private void injectField(Field field, Object target) {
        List<MockHolder> selectedMocks = mocks;
        for (MockSelector<?> selector : selectors) {
            selectedMocks = selector.selectByField(field, selectedMocks);
        }
        injectToFieldWhenSelected(selectedMocks, field, target);
    }

    private void injectToFieldWhenSelected(List<MockHolder> matchingMocks, Field field, Object target) {
        if (notEmpty(matchingMocks)) {
            setField(field, target, matchingMocks.get(0).getMock());
        }
    }

    private boolean notEmpty(List<?> list) {
        return !list.isEmpty();
    }
}
