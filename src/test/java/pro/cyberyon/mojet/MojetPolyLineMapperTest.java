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

import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test
 *
 * @author Guillaume CHAUVET
 */
class MojetPolyLineMapperTest {

	private static interface PojoVisitor extends RecordVisitor {

		void visit(MyPojo visitor);

		void visit(YourPojo visitor);
	}

	@Getter
	@Setter
	@Record
	@Matcher("MY*")
	public static final class MyPojo implements RecordVisitable<PojoVisitor> {
		@Zap(length = 2)
		@Fragment(length = 3, padder = '0', alignement = Fragment.PadWay.LEFT)
		private int value;
		@Fragment(length = 10, alignement = Fragment.PadWay.LEFT)
		private String name;

		@Fragment(length = 10)
		private String bar;

		@Override
		public void accept(PojoVisitor visitor) {
			visitor.visit(this);
		}
	}

	@Getter
	@Setter
	@Record
	@Matcher("YOUR*")
	public static final class YourPojo implements RecordVisitable<PojoVisitor> {
		@Zap(length = 4)
		@Fragment(length = 3, padder = '0')
		private int value;
		@Fragment(length = 5, alignement = Fragment.PadWay.RIGHT)
		private String name;
		@Fragment(length = 5)
		private long foo;

		@Override
		public void accept(PojoVisitor visitor) {
			visitor.visit(this);
		}
	}

	@Test
	void testMultiplePojoTypeReadAndWrite() throws Exception {
		final var mappers = new HashSet<Class<? extends RecordVisitable>>();
		mappers.add(MyPojo.class);
		mappers.add(YourPojo.class);
		final var mapper = new MojetPolyLineMapper<RecordVisitable>(mappers);
		var result = mapper.mapLine("MY123TEST      VALUE     ", 1);
		assertNotNull(result);
		result.accept(new PojoVisitor() {
			@Override
			public void visit(MyPojo visitor) {
				assertEquals(123, visitor.getValue());
				assertEquals("TEST", visitor.getName());
				assertEquals("VALUE     ", visitor.getBar());
			}

			@Override
			public void visit(YourPojo visitor) {
				fail("Not good type");
			}
		});
		result = mapper.mapLine("YOUR567 TEST07890", 2);
		assertNotNull(result);
		result.accept(new PojoVisitor() {
			@Override
			public void visit(MyPojo visitor) {
				fail("Not good type");
			}

			@Override
			public void visit(YourPojo visitor) {
				assertEquals(567, visitor.getValue());
				assertEquals("TEST", visitor.getName());
				assertEquals(7890, visitor.getFoo());
			}
		});
	}

}
