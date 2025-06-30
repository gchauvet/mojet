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
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Guillaume CHAUVET
 */
class MojetLineIT {

	@Test
	void testSimplePojoReadAndWrite() throws Exception {
		final NodesBuilder builder = new NodesBuilder();
		final MojetLineMapper<RootPojo> mapper = new MojetLineMapper(builder, RootPojo.class);
		final RootPojo result = mapper.mapLine("01985000##114273EUR567   100011000210003200301_____", 1);
		final MojetLineAggregator<RootPojo> aggregator = new MojetLineAggregator(builder, RootPojo.class);
		assertEquals("01985000##114273EUR567   100011000210003200301_____", aggregator.aggregate(result));
	}

	private static interface PojoVisitor extends RecordVisitor {
		void visit(MyPojo visitor);

		void visit(YourPojo visitor);
	}

	@Getter
	@Setter
	public static abstract class AbstractPojo implements RecordVisitable<PojoVisitor> {
		@Fragment(length = 3, padder = '0')
		private int value;
		@Fragment(length = 10)
		private String name;
	}

	@Getter
	@Setter
	@Matcher("MY*")
	public static final class MyPojo extends AbstractPojo {
		@Fragment(length = 10)
		private String other;

		@Override
		public void accept(PojoVisitor visitor) {
			visitor.visit(this);
		}
	}

	@Getter
	@Setter
	@Matcher("YOUR*")
	public static final class YourPojo extends AbstractPojo {
		@Fragment(length = 5)
		private long other;

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
		assertEquals(MyPojo.class, mapper.mapLine("MY123TEST      ", 1).getClass());
		assertEquals(YourPojo.class, mapper.mapLine("MY123TEST      ", 2).getClass());
	}

}
