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

import pro.cyberyon.mojet.Record;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;

/**
 *
 * @author Guillaume CHAUVET
 */
public class RecordNode extends AbstractNode<Record> {
    
    private final Set<NodeVisitable> nodes = new LinkedHashSet<>();
    @Getter
    private final Class<?> type;

    public RecordNode(String accessor, Class<?> type) {
        super(type.getAnnotation(Record.class), accessor);
        this.type = type;
    }
    
    public boolean add(NodeVisitable node) {
        return nodes.add(node);
    }
    
    public Set<NodeVisitable> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
    
}
