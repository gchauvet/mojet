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

/**
 * Interface to define the producer side of visitor pattern
 *
 * @param <P> the inherited interface of consumer side
 * @author Guillaume CHAUVET
 */
public interface RecordVisitable<P extends RecordVisitor> {

	/**
	 * Entry point of visitor consumer side
	 *
	 * @param visitor the instance of the visitor
	 */
	void accept(P visitor);

}
