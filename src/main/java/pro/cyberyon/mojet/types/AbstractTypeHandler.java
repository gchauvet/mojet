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
package pro.cyberyon.mojet.types;

import lombok.NonNull;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * partial implementation to every concret TypeHandler.
 *
 * @param <T> type of pojo record
 *
 * @author Guillaume CHAUVET
 */
public abstract class AbstractTypeHandler<T> implements TypeHandler<T> {

	@Override
	public final boolean accept(Class<?> type) {
		boolean result = false;
		if (type != null) {
			if (type.isArray()) {
				type = type.getComponentType();
			}
			if (type.isPrimitive()) {
				type = ClassUtils.primitivesToWrappers(type)[0];
			}
			result = isAccept(type);
		}
		return result;
	}

	@Override
	public final T read(String data, String format) {
		T result = null;
		if (StringUtils.isNotEmpty(data)) {
			result = doRead(data, format);
		}
		return result;
	}

	@Override
	public String write(T data, String format) {
		return data != null ? doWrite(data, format) : "";
	}


	/**
	 * Check type of field (or nested type array)
	 *
	 * @param type non primitive class type to check
	 * @return positive if accepted
	 */
	protected abstract boolean isAccept(Class<?> type);

	/**
	 * Read a non null data
	 *
	 * @param data   the data to read
	 * @param format the optional format
	 * @return readed content
	 */
	protected abstract T doRead(@NonNull String data, String format);

	/**
	 * Write a non null data
	 *
	 * @param data   the data to write
	 * @param format the optional format
	 * @return content to write
	 */
	protected abstract String doWrite(@NonNull T data, String format);

}
