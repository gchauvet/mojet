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

import lombok.NonNull;
import pro.cyberyon.mojet.nodes.RecordNode;

/**
 * Skeleton class providing fields extraction.
 *
 * @author Guillaume CHAUVET
 */
abstract class AbstractMojetLine<T> {

    /**
     * The pojo class type
     */
    protected final RecordNode root;

    /**
     * Constructor to build the list of field in a annoted pojo class
     *
     * @param targetType the pojo class type
     */
    protected AbstractMojetLine(@NonNull Class<T> targetType) {
	this(targetType, new NodesBuilder());

    }

    /**
     * Constructor to build the list of field in a annoted pojo class
     *
     * @param targetType the pojo class type
     * @param builder the builder to use
     */
    protected AbstractMojetLine(@NonNull Class<T> targetType, @NonNull NodesBuilder builder) {
	root = builder.build(targetType);
    }

}
