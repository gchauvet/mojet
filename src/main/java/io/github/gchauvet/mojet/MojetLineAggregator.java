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
package io.github.gchauvet.mojet;

import io.github.gchauvet.mojet.types.TypeHandlerFactory;
import io.github.gchauvet.mojet.types.TypeHandler;
import java.lang.reflect.Field;
import java.util.Map;
import org.apache.commons.text.TextStringBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;

/**
 * This class allow to write type as a line of characters.
 *
 * @param <T> POJO type
 * @author Guillaume CHAUVET
 */
public class MojetLineAggregator<T> extends AbstractMojetLine<T> implements LineAggregator<T> {

    private final BeanWrapperFieldExtractor<T> extractor;

    public MojetLineAggregator(final Class<T> type) {
        super(type);
        extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(mappedFields.keySet().toArray(new String[0]));
    }

    @Override
    public String aggregate(T item) {
        final TextStringBuilder output = new TextStringBuilder();
        final var items = extractor.extract(item);

        int i = 0;
        for (Map.Entry<String, Field> entry : mappedFields.entrySet()) {
            Field field = entry.getValue();
            for (Filler filler : field.getAnnotationsByType(Filler.class)) {
                output.appendPadding(filler.length(), filler.value());
            }

            if (field.isAnnotationPresent(Fragment.class)) {
                final TypeHandler<Object> handler = getHandler(field);

                if (handler.accept(items[i].getClass())) {
                    generateFragment(output, handler, items[i], field);
                } else {
                    throw new MojetRuntimeException("Bad converter on field " + field);
                }
            }
            i++;
        }

        return output.toString();
    }

    private static TypeHandler<Object> getHandler(Field field) {
        final TypeHandler<Object> result;
        final Converter converter = field.getAnnotation(Converter.class);
        if (converter != null) {
            try {
                result = (TypeHandler<Object>) converter.value().getConstructor().newInstance();
            } catch (ReflectiveOperationException ex) {
                throw new MojetRuntimeException(ex);
            }
        } else {
            result = (TypeHandler<Object>) TypeHandlerFactory.getInstance().get(field.getType());
        }
        return result;
    }

    private static void generateFragment(final TextStringBuilder output, TypeHandler<Object> handler, Object item, Field field) {
        final Fragment fragment = field.getAnnotation(Fragment.class);
        final String data = handler.write(item);
        if (data.length() > fragment.length())
            throw new MojetRuntimeException(field.toString() + " length (" + data.length() + ") greater than fragment length definition (" + fragment.length() + ")" );
        final Padding padding = field.getAnnotation(Padding.class);
        final Padding.PadWay way;

        if (padding != null) {
            way = padding.value();
        } else {
            way = Padding.PadWay.LEFT;
        }
        switch (way) {
            case LEFT:
                output.appendFixedWidthPadLeft(data, fragment.length(), fragment.padder());
                break;
            case RIGHT:
                output.appendFixedWidthPadRight(data, fragment.length(), fragment.padder());
                break;
            default:
                throw new MojetRuntimeException("Undefined case");
        }
    }

}
