/*
 * Copyright 2025 Guillaume.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.batch.item.file.transform.LineAggregator;

/**
 * Construct a new aggregator whos can handle multiple visitable records
 *
 * @author Guillaume CHAUVET
 */
public class MojetPolyLineAggregator<T extends RecordVisitable> implements LineAggregator<T> {

	private final Map<Class<? extends T>, MojetLineAggregator<? extends T>> aggregators;

	public MojetPolyLineAggregator(final Set<Class<? extends T>> pojos) {
		final Map<Class<? extends T>, MojetLineAggregator<? extends T>> instances = new HashMap<>();
		final NodesBuilder builder = new NodesBuilder();
		for (Class<? extends T> pojo : pojos) {
			instances.put(pojo, new MojetLineAggregator<>(builder, pojo));
		}
		aggregators = Collections.unmodifiableMap(instances);
	}

	@Override
	public String aggregate(T item) {
		if (!aggregators.containsKey(item.getClass())) {
			throw new MojetRuntimeException("Unknow record class : " + item.getClass());
		}
		return ((MojetLineAggregator<T>) aggregators.get(item)).aggregate(item);
	}

}
