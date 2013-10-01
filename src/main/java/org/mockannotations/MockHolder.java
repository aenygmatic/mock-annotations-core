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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import org.mockannotations.utils.MockAnnotationReflectionUtils;

/**
 * Wrapper class for a mock. It conatains the mock's name, the source field and
 * the mock itself.
 *
 * @author Balazs Berkes
 */
public class MockHolder {

    private static final MockHolder EMPTY_MOCKHOLDER = new MockHolder();

    private Field sourceField;
    private Object mock;
    private String name;

    public static MockHolder emptyMock() {
        return EMPTY_MOCKHOLDER;
    }

    public void setSourceField(Field sourceField) {
        this.sourceField = sourceField;
    }

    public Field getSourceField() {
        return sourceField;
    }

    public String getSourceName() {
        return sourceField == null ? "" : sourceField.getName();
    }

    public void setMock(Object mock) {
        this.mock = mock;
    }

    public Object getMock() {
        return mock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public List<Type> getGenericParameters() {
        return MockAnnotationReflectionUtils.getGenericParameters(sourceField);
    }

    @Override
    public String toString() {
        return "MockHolder{" + "sourceField=" + getSourceName() + ", mock=" + mock + ", name=" + name + '}';
    }
}
