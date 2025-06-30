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

/**
 * Type handler contract to manage java types.
 *
 * @param <T> type of field to handle
 *
 * @author Guillaume CHAUVET
 */
public interface TypeHandler<T> {

	/**
	 * Check if we can handle the data type
	 *
	 * @param type to check
	 * @return <code>true</code> if handlable
	 */
	boolean accept(Class<?> type);

	/**
	 * process an input string to a concrete field value
	 *
	 * @param data   string of data
	 * @param format of data, null otherwise
	 * @return instance of the type
	 */
	T read(String data, String format);

	/**
	 * transform a field value to an output string
	 *
	 * @param data   the type with data
	 * @param format of data, null otherwise
	 * @return string content of type
	 */
	String write(T data, String format);

}
