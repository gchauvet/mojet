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

import org.apache.commons.text.TextStringBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.BeanWrapperImpl;
import pro.cyberyon.mojet.nodes.FillerNode;
import pro.cyberyon.mojet.nodes.FragmentNode;
import pro.cyberyon.mojet.types.TypeHandler;

/**
 * This class allow to write type as a line of characters.
 *
 * @param <T> pojo type
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
	 * Construct a new pojo {@link LineAggregator} instance
	 *
	 * @param builder the node builder instance to use
	 * @param type    the bean type to manage
	 */
	public MojetLineAggregator(final NodesBuilder builder, final Class<T> type) {
		super(type, builder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String aggregate(final T item) {
		final TextStringBuilder output = new TextStringBuilder();
		root.accept(new AbstractNodeVisitor() {

			private final BeanWrapperImpl bean = new BeanWrapperImpl(item);

			@Override
			public void visit(final FillerNode node) {
				output.appendPadding(node.getLength(), node.getPadding());
			}

			@Override
			public void visit(final FragmentNode node) {
				final Object value = bean.getPropertyValue(getPath());
				final String data = ((TypeHandler<Object>) node.getHandler()).write(value, node.getFormat());
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
