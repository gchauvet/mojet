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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import pro.cyberyon.mojet.nodes.*;
import pro.cyberyon.mojet.types.TypeHandler;
import pro.cyberyon.mojet.types.TypeHandlerFactory;

/**
 * A nodes builder
 *
 * @author Guillaume CHAUVET
 */
public class NodesBuilder {

	/**
	 * Cache to avoid multiple resolution time type.
	 */
	private final Map<Class<?>, RecordNode> cache = new HashMap<>();

	/**
	 * Construct an AST from a class definition
	 *
	 * @param type the class definition
	 * @return a Recod not as root element
	 */
	public RecordNode build(Class<?> type) {
		return build("", type);
	}

	private RecordNode build(String accessor, Class<?> type) {
		if (!cache.containsKey(type)) {
			if (!type.isAnnotationPresent(Record.class)) {
				throw new MojetRuntimeException("Record not annoted");
			} else {
				final RecordNode result = new RecordNode(accessor, type);
				for (Field field : type.getDeclaredFields()) {
					build(field, result);
				}
				addFillers(type.getDeclaredAnnotationsByType(Zap.class), result);
				cache.put(type, result);
			}
		}
		return cache.get(type);
	}

	private void build(final Field field, final RecordNode node) {
		addFillers(field.getDeclaredAnnotationsByType(Zap.class), node);
		final String accessor = field.getName();
		final boolean isArray = field.getType().isArray();
		AbstractNode<?> item = null;

		if (field.isAnnotationPresent(Record.class)) {
			item = build(accessor, !isArray ? field.getType() : field.getType().componentType());
		} else if (field.isAnnotationPresent(Fragment.class)) {
			item = processFragment(accessor, field);
		}
		if (item != null) {
			if (isArray) {
				if (field.isAnnotationPresent(Occurences.class)) {
					item = new OccurencesNode(accessor, field.getAnnotation(Occurences.class), item);
				} else {
					throw new MojetRuntimeException("Occurences annotation required");
				}
			}
			node.add(item);
		}
	}

	private AbstractNode<?> processFragment(final String accessor, final Field field) {
		final TypeHandler<?> handler;
		if (field.isAnnotationPresent(Transform.class)) {
			final Transform converter = field.getAnnotation(Transform.class);
			try {
				handler = converter.value().getDeclaredConstructor().newInstance();
			} catch (ReflectiveOperationException ex) {
				throw new MojetRuntimeException("Can't instanciate handler " + converter.value().getSimpleName(), ex);
			}
		} else {
			handler = TypeHandlerFactory.getInstance().get(field.getType());
		}
		if (handler.accept(field.getType())) {
			return new FragmentNode(accessor, field.getAnnotation(Fragment.class), handler);
		} else {
			throw new MojetRuntimeException("Handler can't manage this class type");
		}
	}

	private static void addFillers(Zap[] fillers, RecordNode node) {
		for (Zap filler : fillers) {
			node.add(new ZapNode(filler));
		}
	}

}
