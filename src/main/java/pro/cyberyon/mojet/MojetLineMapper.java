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

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.LineMapper;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import pro.cyberyon.mojet.nodes.ZapNode;
import pro.cyberyon.mojet.nodes.FragmentNode;

/**
 * MojetLineMapper is an implementation of LineMapper that uses a POJO type to
 * map data that are annotated.
 *
 * @param <T> pojo type
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
	 * Construct a new pojo {@link LineMapper} instance
	 *
	 * @param builder    the node builder instance to use
	 * @param targetType the bean type to manage
	 */
	public MojetLineMapper(final NodesBuilder builder, final Class<T> targetType) {
		super(targetType, builder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T mapLine(final String line, int lineNumber) throws Exception {
		final BeanWrapper wrapper = new BeanWrapperImpl(root.getType());
		wrapper.setAutoGrowNestedPaths(true);
		root.accept(new AbstractNodeVisitor() {

			private int index = 0;

			@Override
			public void visit(final ZapNode node) {
				index += node.getLength();
			}

			@Override
			public void visit(final FragmentNode node) {
				String data = line.substring(index, index + node.getLenght());
				switch (node.getAlignement()) {
					case NONE:
						// Do nothing
						break;
					case LEFT:
						data = StringUtils.stripEnd(data, Character.toString(node.getPadder()));
						break;
					case RIGHT:
						data = StringUtils.stripStart(data, Character.toString(node.getPadder()));
						break;
					default:
						throw new MojetRuntimeException("Undefined case");
				}

				final Object value = node.getHandler().read(data, node.getFormat());
				wrapper.setPropertyValue(getPath(), value);
				index += node.getLenght();
			}
		});
		return (T) wrapper.getWrappedInstance();
	}

}
