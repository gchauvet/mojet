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
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Integration test between mappers and aggregators
 *
 * @author Guillaume CHAUVET
 */
class MojetLineIT {

	@Test
	void testSimplePojoReadAndWrite() throws Exception {
		final String line = "01985000##114273EUR567   100011000210003200301_____";
		final NodesBuilder builder = new NodesBuilder();
		final MojetLineMapper<RootPojo> mapper = new MojetLineMapper(builder, RootPojo.class);
		final RootPojo result = mapper.mapLine(line, 1);
		final MojetLineAggregator<RootPojo> aggregator = new MojetLineAggregator(builder, RootPojo.class);
		assertEquals(line, aggregator.aggregate(result));
	}

}
