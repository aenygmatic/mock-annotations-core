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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.mockannotations.MockHolder;

/**
 * Unit test for {@link ByGenericSelector}.
 * <p>
 * @author Balazs Berkes
 */
public class ByGenericSelectorTest {

    private Map<String, Object> stringObjectMap;
    private Map<Integer, String> integerStringMap;
    private Map<String, Integer> stringIntegerMap;
    private List<String> stringList;

    private MockHolder stringObjectHolder;
    private MockHolder integerStringHolder;
    private List<MockHolder> mocks;
    private Field targetField;

    private ByGenericSelector underTest;

    @Before
    public void setUp() throws NoSuchFieldException {
        initializeMocks();
        underTest = new ByGenericSelector();
    }

    @Test
    public void testSelectShouldReturnOnlyMockWithSameGenericParameters() {
        givenMocks(stringObjectHolder, integerStringHolder);
        givenTargetField("stringObjectMap");

        List<MockHolder> matchingMocks = underTest.selectByField(targetField, mocks);

        assertEquals(stringObjectHolder, matchingMocks.get(0));
    }

    @Test
    public void testSelectShouldNotSelectWhenLessGenericParameterInTarget() {
        givenMocks(stringObjectHolder, integerStringHolder);
        givenTargetField("stringList");

        List<MockHolder> matchingMocks = underTest.selectByField(targetField, mocks);

        assertTrue(matchingMocks.isEmpty());
    }

    @Test
    public void testSelectShouldNotSelectWhenGenericParamOrderIsDifferent() {
        givenMocks(stringObjectHolder, integerStringHolder);
        givenTargetField("stringIntegerMap");

        List<MockHolder> matchingMocks = underTest.selectByField(targetField, mocks);

        assertTrue(matchingMocks.isEmpty());
    }

    private void initializeMocks() throws NoSuchFieldException {
        stringObjectMap = new HashMap<String, Object>();
        stringObjectHolder = new MockHolder();
        stringObjectHolder.setSourceField(getFieldByName("stringObjectMap"));

        integerStringMap = new HashMap<Integer, String>();
        integerStringHolder = new MockHolder();
        integerStringHolder.setSourceField(getFieldByName("integerStringMap"));
    }

    private void givenMocks(MockHolder... mocks) {
        this.mocks = Arrays.asList(mocks);
    }

    private void givenTargetField(String name) {
        targetField = getFieldByName(name);
    }

    private Field getFieldByName(String name) {
        Field field = null;
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.getName().equals(name)) {
                field = f;
                break;
            }
        }
        return field;
    }
}
