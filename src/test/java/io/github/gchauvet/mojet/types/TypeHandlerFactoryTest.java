/*
 * Copyright 2025 Guillaume CHAUVET.
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
package io.github.gchauvet.mojet.types;

import io.github.gchauvet.mojet.MojetRuntimeException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of {@link TypeHandlerFactory}
 * @author Guillaume CHAUVET
 */
class TypeHandlerFactoryTest {

    @Test
    void testFactory() {
        final TypeHandlerFactory instance = TypeHandlerFactory.getInstance();
        assertSame(instance, TypeHandlerFactory.getInstance());
    }
    
    @Test
    void testGetter() {
        assertNotNull(TypeHandlerFactory.getInstance().get(long.class));
        assertThrows(MojetRuntimeException.class, () -> TypeHandlerFactory.getInstance().get(getClass()));
    }
    
}
