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
import org.springframework.batch.item.file.LineMapper;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import pro.cyberyon.mojet.nodes.AbstractNode;
import pro.cyberyon.mojet.nodes.FillerNode;
import pro.cyberyon.mojet.nodes.FragmentNode;
import pro.cyberyon.mojet.nodes.NodeVisitor;
import pro.cyberyon.mojet.nodes.OccurencesNode;
import pro.cyberyon.mojet.nodes.RecordNode;

/**
 * MojetLineMapper is an implementation of LineMapper that uses a POJO type to
 * map data that are annotated.
 *
 * @param <T> POJO type
 *
 * @author Guillaume CHAUVET
 */
public class MojetLineMapper<T> extends AbstractMojetLine<T> implements LineMapper<T> {

    /**
     * Construct a new pojo {@link LineMapper} instance
     *
     * @param targetType the bean type to manage
     */
    public MojetLineMapper(final Class<T> targetType) {
	super(targetType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T mapLine(final String line, int lineNumber) throws Exception {
	final BeanWrapper wrapper = new BeanWrapperImpl(root.getType());
	wrapper.setAutoGrowNestedPaths(true);
	root.accept(new NodeVisitor() {

	    private final Deque<String> path = new ArrayDeque<>();
	    private int index = 0;

	    @Override
	    public void visit(final RecordNode node) {
		for (AbstractNode<?> visitable : node.getNodes()) {
		    path.push(visitable.getAccessor());
		    visitable.accept(this);
		    path.pop();
		}
	    }

	    @Override
	    public void visit(final FillerNode node) {
		index += node.getLength();
	    }

	    @Override
	    public void visit(final OccurencesNode node) {
		final String old = path.pop();
		for (int i = 0; i < node.getCount(); i++) {
		    path.push(node.getAccessor() + "[" + i + "]");
		    node.getItem().accept(this);
		    path.pop();
		}
		path.push(old);
	    }

	    @Override
	    public void visit(final FragmentNode node) {
		final String data = line.substring(index, index + node.getLenght());
		final Object value = node.getHandler().read(data, node.getFormat());
		wrapper.setPropertyValue(getPath(), value);
		index += node.getLenght();
	    }

	    private String getPath() {
		return path.stream().filter(t -> !t.isEmpty()).collect(Collectors.collectingAndThen(
			Collectors.toList(),
			lst -> {
			    Collections.reverse(lst);
			    return lst.stream().filter(t -> !t.isEmpty()).collect(Collectors.joining("."));
			}
		));
	    }
	});
	return (T) wrapper.getWrappedInstance();
    }

}
