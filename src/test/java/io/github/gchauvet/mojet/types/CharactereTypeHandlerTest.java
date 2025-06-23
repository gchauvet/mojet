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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of byte type handler
 *
 * @author Guillaume CHAUVET
 */
class CharactereTypeHandlerTest {

    private CharacterTypeHandler instance = new CharacterTypeHandler();

    @Test
    void testAccept() {
        assertFalse(instance.accept(null));
        assertFalse(instance.accept(Double.class));
        assertTrue(instance.accept(char.class));
        assertTrue(instance.accept(Character.class));
    }

    @Test
    void testRead() {
        assertEquals('0', instance.read("0", null));
        assertEquals('t', instance.read("t", null));
    }

    @Test
    void testWrite() {
        assertEquals("t", instance.write('t', null));
        assertEquals("€", instance.write('€', null));
    }

}
