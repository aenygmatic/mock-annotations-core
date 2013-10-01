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

import java.lang.reflect.Field;
import java.util.List;

import org.mockannotations.MockHolder;

/**
 * Provides unified interface for selecting mock according to specific rules.
 * <p>
 * @author Balazs Berkes
 * @param <T> type of object which the mock will be compared to
 */
public interface MockSelector<T> {

    /**
     * Selects the mock according to the rules of implementation.
     * <p>
     * @param selection the reference to compare in the implemented rule
     * @param mocks original list of mocks
     * @return selected list of mocks
     */
    List<MockHolder> select(T selection, List<MockHolder> mocks);

    /**
     * Selects the mock according to the rules of implementation.
     * <p>
     * @param field with and attribute which will be the reference to compare in
     * the implemented rule
     * @param mocks original list of mocks
     * @return selected list of mocks
     */
    List<MockHolder> selectByField(Field field, List<MockHolder> mocks);

}
