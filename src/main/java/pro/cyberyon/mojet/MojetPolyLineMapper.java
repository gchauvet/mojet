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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.support.PatternMatcher;

/**
 * This mapper allow handling of multiple record type.
 *
 * @param <T> visitable pojo type
 * @see PatternMatcher
 * @author Guillaume CHAUVET
 */
public class MojetPolyLineMapper<T extends RecordVisitable> implements LineMapper<T> {

	private final PatternMatcher<MojetLineMapper<? extends T>> matchers;

	/**
	 * Construct a new mapper whos can handle multiple visitable records
	 *
	 * @param pojos set of visitable classes in the same scope
	 */
	public MojetPolyLineMapper(final Set<Class<? extends T>> pojos) {
		final Map<String, MojetLineMapper<? extends T>> mappers = new HashMap<>();
		final NodesBuilder builder = new NodesBuilder();
		for (Class<? extends T> pojo : pojos) {
			mappers.put(pojo.getAnnotation(Matcher.class).value(), new MojetLineMapper<>(builder, pojo));
		}
		matchers = new PatternMatcher<>(mappers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T mapLine(String line, int lineNumber) throws Exception {
		return matchers.match(line).mapLine(line, lineNumber);
	}

}
