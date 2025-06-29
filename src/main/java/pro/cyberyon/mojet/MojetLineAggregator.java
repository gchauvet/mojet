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

import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.commons.text.TextStringBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.BeanWrapperImpl;
import pro.cyberyon.mojet.nodes.AbstractNode;
import pro.cyberyon.mojet.nodes.FillerNode;
import pro.cyberyon.mojet.nodes.FragmentNode;
import pro.cyberyon.mojet.nodes.NodeVisitor;
import pro.cyberyon.mojet.nodes.OccurencesNode;
import pro.cyberyon.mojet.nodes.RecordNode;
import pro.cyberyon.mojet.types.TypeHandler;

/**
 * This class allow to write type as a line of characters.
 *
 * @param <T> POJO type
 * @author Guillaume CHAUVET
 */
public class MojetLineAggregator<T> extends AbstractMojetLine<T> implements LineAggregator<T> {

    /**
     * Construct a new pojo {@link LineAggregator} instance
     *
     * @param type the bean type to manage
     */
    public MojetLineAggregator(final Class<T> type) {
	super(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String aggregate(final T item) {
	final TextStringBuilder output = new TextStringBuilder();
	root.accept(new NodeVisitor() {

	    private final BeanWrapperImpl bean = new BeanWrapperImpl(item);
	    private final Stack<String> path = new Stack<>();

	    private String getPath() {
		return path.stream().filter(t -> !t.isEmpty()).collect(Collectors.joining("."));
	    }

	    @Override
	    public void visit(final RecordNode node) {
		for (AbstractNode visitable : node.getNodes()) {
		    path.push(visitable.getAccessor());
		    visitable.accept(this);
		    path.pop();
		}
	    }

	    @Override
	    public void visit(final FillerNode node) {
		output.appendPadding(node.getLength(), node.getPadding());
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
		final TypeHandler<Object> handler = ((TypeHandler<Object>) node.getHandler());
		final Object value = bean.getPropertyValue(getPath());
		final String data = handler.write(value, node.getFormat());
		if (data.length() > node.getLenght()) {
		    throw new MojetRuntimeException("Data overflow");
		} else {
		    switch (node.getAlignement()) {
			case LEFT:
			    output.appendFixedWidthPadLeft(data, node.getLenght(), node.getPadder());
			    break;
			case RIGHT:
			    output.appendFixedWidthPadRight(data, node.getLenght(), node.getPadder());
			    break;
			default:
			    throw new MojetRuntimeException("Undefined case");
		    }
		}
	    }
	});
	return output.toString();
    }

}
