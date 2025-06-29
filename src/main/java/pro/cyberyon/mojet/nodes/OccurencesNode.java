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
package pro.cyberyon.mojet.nodes;

import lombok.Getter;
import pro.cyberyon.mojet.MojetRuntimeException;
import pro.cyberyon.mojet.Occurences;

/**
 *
 * @author Guillaume CHAUVET
 */
public class OccurencesNode extends AbstractNode<Occurences> {

    @Getter
    private final NodeVisitable item;

    public OccurencesNode(String accessor, Occurences annotation, NodeVisitable item) {
        super(annotation, accessor);
        this.item = item;
	if (getCount() < 1)
	    throw new MojetRuntimeException("Iteration must be a positive value");
    }
    
    public int getCount() {
        return annotation.value();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
