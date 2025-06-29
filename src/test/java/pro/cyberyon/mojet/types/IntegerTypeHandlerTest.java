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
package pro.cyberyon.mojet.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of integer type handler
 *
 * @author Guillaume CHAUVET
 */
class IntegerTypeHandlerTest {

    private final IntegerTypeHandler instance = new IntegerTypeHandler();

    @Test
    void testAccept() {
	assertFalse(instance.accept(null));
	assertFalse(instance.accept(Double.class));
	assertFalse(instance.accept(long.class));
	assertTrue(instance.accept(Integer.class));
	assertTrue(instance.accept(int.class));
    }

    @Test
    void testRead() {
	assertEquals(1, instance.read("1", null));
	assertEquals(1985, instance.read("1985", null));
    }

    @Test
    void testWrite() {
	assertEquals("0", instance.write(0, null));
	assertEquals("1985", instance.write(1985, null));
    }

}
