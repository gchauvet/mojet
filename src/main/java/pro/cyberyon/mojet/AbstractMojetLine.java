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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.stream.Collectors;
import lombok.NonNull;
import pro.cyberyon.mojet.nodes.AbstractNode;
import pro.cyberyon.mojet.nodes.NodeVisitor;
import pro.cyberyon.mojet.nodes.OccurencesNode;
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

    /**
     * Abstract node visitor implemention, providing common behaviors
     */
    protected abstract static class AbstractNodeVisitor implements NodeVisitor {

	private final Deque<String> path = new ArrayDeque<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final RecordNode node) {
	    for (AbstractNode<?> visitable : node.getNodes()) {
		path.push(visitable.getAccessor());
		visitable.accept(this);
		path.pop();
	    }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final OccurencesNode node) {
	    final String old = path.pop();
	    for (int i = 0; i < node.getCount(); i++) {
		path.push(node.getAccessor() + "[" + i + "]");
		node.getItem().accept(this);
		path.pop();
	    }
	    path.push(old);
	}

	/**
	 * Get the path from root record
	 *
	 * @return the absolute path from root record
	 */
	protected final String getPath() {
	    return path.stream().filter(t -> !t.isEmpty()).collect(Collectors.collectingAndThen(
		    Collectors.toList(),
		    lst -> {
			Collections.reverse(lst);
			return lst.stream().filter(t -> !t.isEmpty()).collect(Collectors.joining("."));
		    }
	    ));
	}
    }

}
