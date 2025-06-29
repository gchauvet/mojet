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
 * Unit test of byte type handler
 *
 * @author Guillaume CHAUVET
 */
class ByteTypeHandlerTest {

    private ByteTypeHandler instance = new ByteTypeHandler();

    @Test
    void testAccept() {
	assertFalse(instance.accept(null));
	assertFalse(instance.accept(Double.class));
	assertFalse(instance.accept(char.class));
	assertTrue(instance.accept(byte.class));
	assertTrue(instance.accept(Byte.class));
    }

    @Test
    void testRead() {
	assertEquals((byte) 77, instance.read("77", null));
	assertEquals((byte) 0, instance.read("0", null));
    }

    @Test
    void testWrite() {
	assertEquals("55", instance.write(Byte.valueOf("55"), null));
	assertEquals("-10", instance.write(Byte.parseByte("-10"), null));
    }

}
