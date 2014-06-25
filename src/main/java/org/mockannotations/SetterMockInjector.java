/*
 * Copyright 2014 Balazs Berkes.
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

import static org.mockannotations.utils.MockAnnotationReflectionUtils.getAllSetters;
import static org.mockannotations.utils.MockAnnotationReflectionUtils.getGenericParameters;
import static org.mockannotations.utils.MockAnnotationReflectionUtils.setBySetter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

import org.mockannotations.selection.ByGenericSelector;
import org.mockannotations.selection.ByNameSelector;
import org.mockannotations.selection.ByTypeSelector;
import org.mockannotations.selection.MockSelector;

/**
 * Injects the the given mocks into the target class using it's setters. Mocks are injected by type and name.
 * <p>
 * @author Balazs Berkes
 */
public class SetterMockInjector {

    private static final MockSelector<Class<?>> byTypeSelectior = ByTypeSelector.getSingleton();
    private static final MockSelector<List<Type>> byGenericSelectior = ByGenericSelector.getSingleton();
    private static final MockSelector<String> byNameSelectior = ByNameSelector.getSingleton();

    private List<MockHolder> mocks;

    public SetterMockInjector(List<MockHolder> mocks) {
        this.mocks = mocks;
    }

    /**
     * Injects the previously given mock into the target object.
     * <p>
     * @param target object to be injected
     * @return the target object
     */
    public Object injectTo(Object target) {
        for (Method method : getAllSetters(target.getClass())) {
            injectViaSetter(method, target);
        }
        return target;
    }

    private void injectViaSetter(Method method, Object target) {
        List<MockHolder> selectedMocks = mocks;
        Parameter parameter = method.getParameters()[0];

        selectedMocks = byTypeSelectior.select(parameter.getType(), selectedMocks);

        selectedMocks = byGenericSelectior.select(getGenericParametersOf(parameter), selectedMocks);

        selectedMocks = byNameSelectior.select(getFieldNameOf(method.getName()), selectedMocks);

        injectToSetterWhenSelected(selectedMocks, method, target);

    }

    private List<Type> getGenericParametersOf(Parameter parameter) {
        return getGenericParameters(parameter.getParameterizedType());
    }

    private void injectToSetterWhenSelected(List<MockHolder> matchingMocks, Method setter, Object target) {
        if (notEmpty(matchingMocks)) {
            setBySetter(setter, target, matchingMocks.get(0).getMock());
        }
    }

    private String getFieldNameOf(String setterName) {
        return Character.toLowerCase(setterName.charAt(3)) + setterName.substring(4);
    }

    private boolean notEmpty(List<?> list) {
        return !list.isEmpty();
    }
}
