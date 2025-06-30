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

import pro.cyberyon.mojet.types.AbstractTypeHandler;
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
class MojetLineAggregatorTest {

	public static interface SimplePojoVisitor extends RecordVisitor {

		void visit(SimplePojo instance);
	}

	@Data
	@Record
	@Filler(length = 3, value = '€')
	public static final class SimplePojo implements RecordVisitable<SimplePojoVisitor> {

		@Fragment(length = 7, padder = '0')
		private long id;
		@Filler(length = 3, value = '#')
		@Filler(length = 2, value = '|')
		@Fragment(length = 10)
		private String name;
		@Fragment(length = 10, padder = '_', alignement = Fragment.PadWay.RIGHT)
		private String surname;
		@Converter(MyLocalDateTypeHandler.class)
		@Fragment(length = 4, format = "yyMM")
		private LocalDate date;
		@Fragment(length = 2, padder = '$')
		private byte octet = 5;
		@Fragment(length = 2, padder = '€')
		private char car = 'C';
		@Fragment(length = 5)
		@Occurences(3)
		private long[] values = new long[]{2, 4, 6};

		@Override
		public void accept(SimplePojoVisitor visitor) {
			visitor.visit(this);
		}
	}

	@Test
	void testSimpleAggregation() {
		var instance = new MojetLineAggregator<>(SimplePojo.class);
		SimplePojo item = new SimplePojo();
		item.setId(777);
		item.setName("CHAUVET");
		item.setSurname("Guillaume");
		item.setDate(LocalDate.of(1999, Month.JULY, 18));
		assertEquals("0000777###||   CHAUVETGuillaume_9907$5€C    2    4    6€€€", instance.aggregate(item));
	}

	public static interface VoidVisitor extends RecordVisitor {

		void visit(Void instance);
	}

	@Data
	@Record
	public static final class UndefinedPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 10)
		private Object undefined = "";

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	@Data
	@Record
	public static final class BadFragmentPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 0)
		private long undefined;

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	public static class BuggerConverterType extends AbstractTypeHandler<Object> {

		public BuggerConverterType() {
			throw new IllegalStateException("bug");
		}

		@Override
		protected boolean isAccept(Class<?> type) {
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
	public static final class BuggedConverterPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 10)
		@Converter(BuggerConverterType.class)
		private Object undefined = "";

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	@Data
	@Record
	public static final class NoConverterPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 10)
		private Object undefined = "";

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	public static class InacceptableConverterType extends AbstractTypeHandler<Object> {

		@Override
		protected boolean isAccept(Class<?> type) {
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
	public static final class InacceptablePojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 10)
		@Converter(InacceptableConverterType.class)
		private Object undefined = "";

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	@Data
	@Record
	public static final class OverflowPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 2)
		private String good = "OK";
		@Fragment(length = 3)
		private String overflow = "TEST";

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	@Data
	@Record
	public static final class NoIterableAllowedPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 5)
		@Occurences(3)
		private Iterable<Long> undefined;

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	@Data
	@Record
	public static final class InvalidFragmentPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = -1)
		private String value;

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	@Data
	@Record
	public static final class NoOccurencesDefinedPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 5)
		private long[] values;

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}
	}

	@Data
	@Record
	public static final class BadOccurencesDefinedPojo implements RecordVisitable<VoidVisitor> {

		@Fragment(length = 5)
		@Occurences(0)
		private long[] values;

		@Override
		public void accept(VoidVisitor visitor) {
			// Nothing to do
		}

	}

	@Test
	void testErrorsAggregation() {
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(UndefinedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(BuggedConverterPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(NoConverterPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(InacceptablePojo.class));
		var overflow = new MojetLineAggregator<>(OverflowPojo.class);
		var pojo5 = new OverflowPojo();
		assertEquals("Data overflow", assertThrows(MojetRuntimeException.class, () -> overflow.aggregate(pojo5)).getMessage());
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(NoIterableAllowedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(NoOccurencesDefinedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(BadOccurencesDefinedPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(BadFragmentPojo.class));
		assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(InvalidFragmentPojo.class));
	}

}
