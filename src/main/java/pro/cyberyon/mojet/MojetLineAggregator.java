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

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.text.TextStringBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.BeanWrapperImpl;
import pro.cyberyon.mojet.nodes.FillerNode;
import pro.cyberyon.mojet.nodes.FragmentNode;
import pro.cyberyon.mojet.nodes.NodeVisitable;
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

    @Override
    public String aggregate(final T item) {
        final TextStringBuilder output = new TextStringBuilder();
        root.accept(new NodeVisitor() {

            private static interface PropertyFacade {

                Object getValue(String key);
            }

            private static class BeanPropertyFacade implements PropertyFacade {

                private final BeanWrapperImpl wrapper;

                BeanPropertyFacade(Object object) {
                    wrapper = new BeanWrapperImpl(object);
                }

                @Override
                public Object getValue(String key) {
                    return wrapper.getPropertyValue(key);
                }

            }

            private PropertyFacade current = new BeanPropertyFacade(item);

            @Override
            public void visit(final RecordNode node) {
                final PropertyFacade previous = current;
                if (!node.getAccessor().isEmpty()) {
                    current = new BeanPropertyFacade(item);
                }
                for (NodeVisitable visitable : node.getNodes()) {
                    visitable.accept(this);
                }
                current = previous;
            }

            @Override
            public void visit(final FillerNode node) {
                output.appendPadding(node.getLength(), node.getPadding());
            }

            @Override
            public void visit(final OccurencesNode node) {
                final PropertyFacade previous = current;
                final AtomicInteger counter = new AtomicInteger(0);
                current = new PropertyFacade() {
                    @Override
                    public Object getValue(String key) {
                        return previous.getValue(key + "[" + counter.get() + "]");
                    }
                };
                for (int i = 0; i < node.getCount(); i++) {
                    counter.set(i);
                    node.getItem().accept(this);
                }
                current = previous;
            }

            @Override
            public void visit(final FragmentNode node) {
		final TypeHandler<Object> handler = ((TypeHandler<Object>) node.getHandler());
		final Object value = current.getValue(node.getAccessor());
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
