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
import pro.cyberyon.mojet.types.TypeHandler;

/**
 * Unit test
 *
 * @author Guillaume CHAUVET
 */
class MojetLineAggregatorTest {

	@Data
	@Record
	@Zap(length = 3, value = '€')
	public static final class SimplePojo {

		@Fragment(length = 7, padder = '0', alignement = Fragment.PadWay.LEFT)
		private long id;
		@Zap(length = 3, value = '#')
		@Zap(length = 2, value = '|')
		@Fragment(length = 10, alignement = Fragment.PadWay.LEFT)
		private String name;
		@Fragment(length = 10, padder = '_', alignement = Fragment.PadWay.RIGHT)
		private String surname;
		@Transform(MyLocalDateTypeHandler.class)
		@Fragment(length = 4, format = "yyMM")
		private LocalDate date;
		@Fragment(length = 1, padder = '$')
		private byte octet = 5;
		@Fragment(length = 1, padder = '€')
		private char car = 'C';
		@Fragment(length = 5, alignement = Fragment.PadWay.LEFT)
		@Occurences(3)
		private long[] values = new long[]{2, 4, 6};
		@Fragment(length = 4, truncable = true)
		private String bounded;
		@Fragment(length = 1, alignement = Fragment.PadWay.RIGHT)
		@Occurences(3)
		private final int[] empties = new int[3];
	}

	@Test
	void testSimpleAggregation() {
		var instance = new MojetLineAggregator<>(SimplePojo.class);
		SimplePojo item = new SimplePojo();
		item.setId(777);
		item.setName("CHAUVET");
		item.setSurname("Guillaume");
		item.setDate(LocalDate.of(1999, Month.JULY, 18));
		item.setBounded("TESTAAAAAA");
		assertEquals("7770000###||CHAUVET   _Guillaume99075C2    4    6    TEST000€€€", instance.aggregate(item));
	}

	@Data
	@Record
	public static final class UndefinedPojo {

		@Fragment(length = 10)
		private Object undefined = "";
	}

	@Data
	@Record
	public static final class BadFragmentPojo {

		@Fragment(length = 0)
		private long undefined;
	}

	private static class BuggerConverterType implements TypeHandler<Object> {

		public BuggerConverterType() {
			throw new IllegalStateException("bug");
		}

		@Override
		public boolean accept(Class<?> type) {
			return true;
		}

		@Override
		public Object read(String data, String format) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String write(Object data, String format) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	@Data
	@Record
	public static final class BuggedConverterPojo {

		@Fragment(length = 10)
		@Transform(BuggerConverterType.class)
		private Object undefined = "";
	}

	@Data
	@Record
	public static final class NoConverterPojo {

		@Fragment(length = 10)
		private Object undefined = "";
	}

	private static class InacceptableConverterType implements TypeHandler<Object> {

		@Override
		public boolean accept(Class<?> type) {
			return false;
		}

		@Override
		public Object read(String data, String format) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String write(Object data, String format) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	@Data
	@Record
	public static final class InacceptablePojo {

		@Fragment(length = 10)
		@Transform(InacceptableConverterType.class)
		private Object undefined = "";
	}

	@Data
	@Record
	public static final class OverflowPojo {

		@Fragment(length = 2)
		private String good = "OK";
		@Fragment(length = 3)
		private String overflow = "TEST";
	}

	@Data
	@Record
	public static final class NoIterableAllowedPojo {

		@Fragment(length = 5)
		@Occurences(3)
		private Iterable<Long> undefined;
	}

	@Data
	@Record
	public static final class InvalidFragmentPojo {

		@Fragment(length = -1)
		private String value;
	}

	@Data
	@Record
	public static final class NoOccurencesDefinedPojo {

		@Fragment(length = 5)
		private long[] values;
	}

	@Data
	@Record
	public static final class BadOccurencesDefinedPojo {

		@Fragment(length = 5)
		@Occurences(0)
		private long[] values;
	}

	@Data
	@Record
	public static final class BadBoundedPojo {

		@Fragment(length = 5)
		private String value;
	}

	@Test
	void testErrorsAggregation() {
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(UndefinedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(BuggedConverterPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(NoConverterPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(InacceptablePojo.class));
		var overflow = new MojetLineAggregator<>(OverflowPojo.class);
		var pojo = new OverflowPojo();
		assertEquals("Data overflow", assertThrows(MojetRuntimeException.class, () -> overflow.aggregate(pojo)).getMessage());
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(NoIterableAllowedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(NoOccurencesDefinedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(BadOccurencesDefinedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(BadFragmentPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(InvalidFragmentPojo.class));
		var bounded = new MojetLineAggregator<>(BadBoundedPojo.class);
		var pojo2 = new BadBoundedPojo();
		pojo2.setValue("T");
		assertThrows(MojetRuntimeException.class, () -> bounded.aggregate(pojo2));

	}

	@Data
	@Record
	public static final class AllowPrivateTypeHandlerPojo {

		private static class InacceptableConverterType implements TypeHandler<String> {

			@Override
			public boolean accept(Class<?> type) {
				return true;
			}

			@Override
			public String read(String data, String format) {
				return data;
			}

			@Override
			public String write(String data, String format) {
				return data;
			}

		}


		@Transform(InacceptableConverterType.class)
		@Fragment(length = 5, alignement = Fragment.PadWay.RIGHT)
		private String value;
	}

	@Test
	void testPrivateConverter() {
		var converter = new MojetLineAggregator<>(AllowPrivateTypeHandlerPojo.class);
		var result = new AllowPrivateTypeHandlerPojo();
		result.setValue("TEST");
		assertEquals(" TEST", converter.aggregate(result));
	}

}
