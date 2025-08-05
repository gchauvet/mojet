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

import java.util.Set;
import lombok.Data;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Integration test between mappers and aggregators
 *
 * @author Guillaume CHAUVET
 */
class MojetTest {

	@Data
	@Record
	private static class FooPojo implements RecordVisitable<CustomVisitor> {

		@Zap(value = 'F', length = 1)
		@Zap(value = 'O', length = 2)
		@Fragment(length = 10, alignement = Fragment.PadWay.RIGHT)

		private String test;

		@Override
		public void accept(CustomVisitor visitor) {
			visitor.visit(this);
		}

	}

	@Data
	@Record
	private static class BarPojo implements RecordVisitable<CustomVisitor> {

		@Zap(value = 'B', length = 1)
		@Zap(value = 'A', length = 1)
		@Zap(value = 'R', length = 1)
		@Fragment(length = 20, padder = '0', alignement = Fragment.PadWay.RIGHT)
		private long value;

		@Override
		public void accept(CustomVisitor visitor) {
			visitor.visit(this);
		}
	}

	@Record
	private static class ZarbPojo extends BarPojo {

	}

	private interface CustomVisitor extends RecordVisitor {

		void visit(FooPojo instance);

		void visit(BarPojo instance);
	}

	@Test
	void testSimplePojoReadAndWrite() throws Exception {
		final String line = "01985000##114273EUR567   100011000210003 200301114273NZD000000USD        _____";
		final NodesBuilder builder = new NodesBuilder();
		final MojetLineMapper<RootPojo> mapper = new MojetLineMapper(builder, RootPojo.class);
		final RootPojo result = mapper.mapLine(line, 1);
		final MojetLineAggregator<RootPojo> aggregator = new MojetLineAggregator(builder, RootPojo.class);
		assertEquals("19850000##114273EUR567   100011000210003 200301114273NZD000000USD20250729_____", aggregator.aggregate(result));
	}

	@Test
	void testPolyLineAggregator() {
		final MojetPolyLineAggregator<RecordVisitable<CustomVisitor>> aggregator = new MojetPolyLineAggregator<RecordVisitable<CustomVisitor>>(Set.of(FooPojo.class, BarPojo.class));
		final var foo = new FooPojo();
		foo.setTest("TEST");
		assertEquals("FOO      TEST", aggregator.aggregate(foo));
		final var bar = new BarPojo();
		bar.setValue(18071985);
		assertEquals("BAR00000000000018071985", aggregator.aggregate(bar));
		assertThrows(MojetRuntimeException.class, () -> aggregator.aggregate(new ZarbPojo()));
	}

}
