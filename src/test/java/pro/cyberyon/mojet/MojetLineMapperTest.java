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
package pro.cyberyon.mojet;

import java.time.LocalDate;
import java.time.Month;
import lombok.Data;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test
 *
 * @author Guillaume CHAUVET
 */
class MojetLineMapperTest {

	@Test
	void testSimpleReadLineToRootPojo() throws Exception {
		final MojetLineMapper<RootPojo> mapper = new MojetLineMapper(RootPojo.class);
		final RootPojo result = mapper.mapLine("01985000##114273EUR567   100011000210003200301_____", 1);
		assertEquals(1985, result.getId());
		assertEquals(114273, result.getChild().getTotal());
		assertEquals("EUR", result.getChild().getLabel());
		assertEquals(567, result.getCounter());
		assertArrayEquals(new long[]{10001, 10002, 10003}, result.getValues());
		assertEquals(LocalDate.of(2003, Month.JANUARY, 1), result.getDate());
	}

	@Data
	@Record
	public static class BadFragmentPojo {

		@Fragment(length = -1)
		private int value1;
		@Fragment(length = 0)
		private int value2;
	}

	@Data
	@Record
	public static class BadPaddingPojo {

		@Zap(length = -1)
		private int value1;
		@Zap(length = 0)
		private int value2;
	}

	public static class FakePojo {

		@Fragment(length = 3)
		private String test;
	}

	@Data
	@Record
	public static class BadRecordPojo {

		@Record
		private FakePojo value2;
	}

	@Test
	void testBadPojoSetting() {
		assertThrows(MojetRuntimeException.class, () -> new MojetLineMapper(BadFragmentPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineMapper(BadPaddingPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineMapper(BadRecordPojo.class));
	}

}
