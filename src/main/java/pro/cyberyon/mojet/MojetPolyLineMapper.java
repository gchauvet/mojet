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

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.support.PatternMatcher;

/**
 *
 * @author Guillaume CHAUVET
 */
public class MojetPolyLineMapper<T extends RecordVisitable<?>> implements LineMapper<T> {

	private final PatternMatcher<MojetLineMapper<T>> matchers;

	public MojetPolyLineMapper(final Set<Class<MojetLineMapper<T>>> pojos) {
		final Map<String, MojetLineMapper<T>> mappers = new HashMap<>();
		final NodesBuilder builder = new NodesBuilder();

		try {
			for (Class<MojetLineMapper<T>> pojo : pojos) {
				final Class<T> type = (Class<T>) ((ParameterizedType) pojo.getGenericSuperclass()).getActualTypeArguments()[0];
				mappers.put(pojo.getAnnotation(Matcher.class).value(), pojo.getConstructor(NodesBuilder.class, Class.class).newInstance(builder, type));
			}
			matchers = new PatternMatcher<>(mappers);
		} catch (ReflectiveOperationException ex) {
			throw new MojetRuntimeException("Can't build a mapper", ex);
		}
	}

	@Override
	public T mapLine(String line, int lineNumber) throws Exception {
		return matchers.match(line).mapLine(line, lineNumber);
	}

}
