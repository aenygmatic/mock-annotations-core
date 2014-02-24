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

import static org.mockannotations.utils.MockAnnotationReflectionUtils.getInheritanceDistance;
import static org.mockannotations.utils.MockAnnotationValidationUtils.isNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.mockannotations.MockHolder;

/**
 * Selects the the mocks which are the closest.
 * <p>
 * @author Balazs Berkes
 */
public class ByTypeSelector implements MockSelector<Class<?>> {

    private static final int MAX_DEPTH = Integer.MAX_VALUE;
    private static MockSelector<Class<?>> singleton;

    public static synchronized MockSelector<Class<?>> getSingleton() {
        if (isNull(null)) {
            singleton = new ByTypeSelector();
        }
        return singleton;
    }

    @Override
    public List<MockHolder> selectByField(Field field, List<MockHolder> mocks) {
        return select(field.getType(), mocks);
    }

    @Override
    public List<MockHolder> select(Class<?> selection, List<MockHolder> mocks) {
        List<MockHolder> closestMocks = new ArrayList<MockHolder>();
        int closestDist = MAX_DEPTH;
        for (MockHolder mock : mocks) {
            final int currentDist = getInheritanceDistance(mock.getMock(), selection);
            if (isInstance(currentDist) && isCloserThanCurrent(currentDist, closestDist)) {
                closestDist = currentDist;
                closestMocks.clear();
                closestMocks.add(mock);
            } else if (isInstance(currentDist) && isCloseAsCurrent(currentDist, closestDist)) {
                closestMocks.add(mock);
            }
        }

        return clearIfNoInstanceFound(closestDist, closestMocks);
    }

    private boolean isInstance(final int dist) {
        return dist != -1;
    }

    private boolean isCloserThanCurrent(final int dist, int inheritanceDistance) {
        return dist < inheritanceDistance;
    }

    private boolean isCloseAsCurrent(final int dist, int inheritanceDistance) {
        return dist == inheritanceDistance;
    }

    private List<MockHolder> clearIfNoInstanceFound(int closestDist, List<MockHolder> closestMocks) {
        if (closestDist == MAX_DEPTH) {
            closestMocks.clear();
        }
        return closestMocks;
    }
}
