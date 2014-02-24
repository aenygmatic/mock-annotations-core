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
import static org.mockito.Mockito.when;

import static org.mockannotations.selection.ByNameSelector.NAME_CONTAINS_STRATEGY;
import static org.mockannotations.selection.ByNameSelector.NAME_EQUALS_IGNORE_CASE_STRATEGY;
import static org.mockannotations.selection.ByNameSelector.NAME_EQUALS_STRATEGY;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockannotations.MockHolder;

/**
 * Unit test for {@link ByNameSelector}.
 * <p>
 * @author Balazs Berkes
 */
public class ByNameSelectorTest {

    private List<MockHolder> mocks;
    @Mock
    private MockHolder mockHolder;
    @Mock
    private MockHolder holder;
    @Mock
    private MockHolder lowercasemock;

    private ByNameSelector underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new ByNameSelector();
    }

    @Test
    public void testSelectDefaultStrategyShouldSelectEqualName() {
        givenMockHolderFields();

        List<MockHolder> actual = underTest.select("mockHoder", mocks);

        assertEquals(mockHolder, firstElementOf(actual));
    }

    @Test
    public void testSelectDefaultStrategyShouldSelectEqualIgnoreCaseName() {
        givenMockHolderFields();

        List<MockHolder> actual = underTest.select("lowerCaseMock", mocks);

        assertEquals(lowercasemock, firstElementOf(actual));
    }

    @Test
    public void testSelectDefaultStrategyShouldSelectMockHolderWhenItsNameContainedInFieldsName() {
        givenMockHolderFields();

        List<MockHolder> actual = underTest.select("holderInName", mocks);

        assertEquals(holder, firstElementOf(actual));
    }

    @Test
    public void testSelectShouldReturnFirstMockWhenNoMatchingOne() {
        givenMockHolderFields();

        List<MockHolder> actual = underTest.select("noSuchField", mocks);

        assertEquals(mockHolder, firstElementOf(actual));
    }

    @Test
    public void testSelectShouldReturnEmptyMocksWhenMockListIsEmpty() {
        List<MockHolder> mockList = Collections.EMPTY_LIST;

        List<MockHolder> actual = underTest.select("name", mockList);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testOverrideStrategy() {
        givenMockHolderFields();

        ByNameSelector.overrideStrategy(NAME_CONTAINS_STRATEGY);
        List<MockHolder> actual = underTest.select("mo", mocks);

        assertTrue(actual.size() == 1);
        assertTrue(actual.contains(mockHolder));
        ByNameSelector.overrideStrategy(NAME_EQUALS_STRATEGY, NAME_EQUALS_IGNORE_CASE_STRATEGY, NAME_CONTAINS_STRATEGY);
    }

    private void givenMockHolderFields() {
        mocks = new ArrayList<MockHolder>();
        when(mockHolder.getSourceName()).thenReturn("mockHolder");
        when(holder.getSourceName()).thenReturn("holder");
        when(lowercasemock.getSourceName()).thenReturn("lowercasemock");
        mocks.add(mockHolder);
        mocks.add(holder);
        mocks.add(lowercasemock);
    }

    private MockHolder firstElementOf(List<MockHolder> actual) {
        return actual.get(0);
    }
}
