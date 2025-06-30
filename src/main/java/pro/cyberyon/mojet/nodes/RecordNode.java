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

import pro.cyberyon.mojet.Record;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;

/**
 * Node representing a record set
 *
 * @author Guillaume CHAUVET
 */
public class RecordNode extends AbstractNode<Record> {

	private final Set<AbstractNode<?>> nodes = new LinkedHashSet<>();
	@Getter
	private final Class<?> type;

	/**
	 * Construct a node record
	 *
	 * @param accessor field name of accessor
	 * @param type     class type or record
	 */
	public RecordNode(final String accessor, final Class<?> type) {
		super(type.getAnnotation(Record.class), accessor);
		this.type = type;
	}

	/**
	 * Add a node to record (order keeped)
	 *
	 * @param node abstract node to add
	 * @return if added
	 */
	public boolean add(AbstractNode<?> node) {
		return nodes.add(node);
	}

	/**
	 * Ordered nodes of record
	 *
	 * @return nodes
	 */
	public Set<AbstractNode<?>> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
