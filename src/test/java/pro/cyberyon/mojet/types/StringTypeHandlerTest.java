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
 * Unit test of string type handler
 *
 * @author Guillaume CHAUVET
 */
class StringTypeHandlerTest {

    private StringTypeHandler instance = new StringTypeHandler();

    @Test
    void testAccept() {
	assertFalse(instance.accept(null));
	assertFalse(instance.accept(Double.class));
	assertTrue(instance.accept(String.class));
    }

    @Test
    void testRead() {
	assertEquals("", instance.read("", null));
	assertEquals("test", instance.read("test", null));
	assertEquals("€éàÉ", instance.read("€éàÉ", null));
    }

    @Test
    void testWrite() {
	assertEquals("", instance.write("", null));
	assertEquals("test", instance.write("test", null));
	assertEquals("€éàÉ", instance.write("€éàÉ", null));
    }

}
