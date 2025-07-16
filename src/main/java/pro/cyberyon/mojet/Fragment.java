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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pro.cyberyon.mojet.types.TypeHandler;

/**
 * Define a fragment of a record. Used to indicate the length of the field in a
 * line of data.
 *
 * @author Guillaume CHAUVET
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Fragment {

	/**
	 * Define ways for padding
	 */
	public enum PadWay {

		/**
		 * No alignment
		 */
		NONE,
		/**
		 * align at left
		 */
		LEFT,
		/**
		 * align at right
		 */
		RIGHT
	}

	/**
	 * The field length
	 *
	 * @return a positive number corresponding to field length
	 */
	int length();

	/**
	 * The default padding character
	 *
	 * @return the padding character, with space as default
	 */
	char padder() default ' ';

	/**
	 * An optional argument
	 *
	 * @return optional format argument passed to the {@link TypeHandler}
	 */
	String format() default "";

	/**
	 * The padding way to apply
	 *
	 * @return padding way constant enum
	 */
	PadWay alignement() default PadWay.NONE;

	/**
	 * Allow unparesable/no value
	 *
	 * @return <code>true>/code> if optional
	 */
	boolean optional() default false;
}
