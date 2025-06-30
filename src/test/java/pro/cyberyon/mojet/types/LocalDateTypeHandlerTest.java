/*
 * Copyright 2025 Guillaume CHAUVET.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.cyberyon.mojet.types;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of long type handler
 *
 * @author Guillaume CHAUVET
 */
class LocalDateTypeHandlerTest {

	private LocalDateTypeHandler instance = new LocalDateTypeHandler();

	@Test
	void testAccept() {
		assertFalse(instance.accept(null));
		assertFalse(instance.accept(Double.class));
		assertFalse(instance.accept(Date.class));
		assertTrue(instance.accept(LocalDate.class));
	}

	@Test
	void testRead() {
		assertEquals(LocalDate.of(2002, Month.OCTOBER, 5), instance.read("2002-10-05", null));
		assertEquals(LocalDate.of(2003, Month.FEBRUARY, 1), instance.read("01-02-03", "dd-MM-uu"));
	}

	@Test
	void testWrite() {
		assertEquals("2001-03-17", instance.write(LocalDate.of(2001, Month.MARCH, 17), null));
		assertEquals("20010317", instance.write(LocalDate.of(2001, Month.MARCH, 17), "yyyyMMdd"));
	}

}
