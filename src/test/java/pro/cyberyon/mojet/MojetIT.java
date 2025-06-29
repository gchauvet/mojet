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
package pro.cyberyon.mojet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Guillaume CHAUVET
 */
class MojetIT {

    @Test
    void testSimpleReadLineToRootPojo() throws Exception {
	final NodesBuilder builder = new NodesBuilder();
	final MojetLineMapper<RootPojo> mapper = new MojetLineMapper(builder, RootPojo.class);
	final RootPojo result = mapper.mapLine("01985000##114273EUR567   100011000210003200301_____", 1);
	final MojetLineAggregator<RootPojo> aggregator = new MojetLineAggregator(builder, RootPojo.class);
	assertEquals("01985000##114273EUR567   100011000210003200301_____", aggregator.aggregate(result));
    }

}
