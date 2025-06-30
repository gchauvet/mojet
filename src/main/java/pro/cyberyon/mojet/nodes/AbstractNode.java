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
package pro.cyberyon.mojet.nodes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * skeleton class for node's implementation
 *
 * @param <A> the annotation to handle
 * @author Guillaume CHAUVET
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNode<A> implements NodeVisitable {

	/**
	 * the annotation to decorate
	 */
	protected final A annotation;

	/**
	 * The accessor field name
	 */
	@Getter
	protected final String accessor;

}
