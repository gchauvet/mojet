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
     * default constructor to build the list of field in a annoted pojo class
     *
     * @param targetType the pojo class type
     */
    protected AbstractMojetLine(@NonNull Class<T> targetType) {
	final NodesBuilder builder = new NodesBuilder();
	root = builder.build(targetType);
    }

}
