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

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.Map.Entry;

/**
 * MojetLineMapper is an implementation of LineMapper that uses a POJO type to
 * map data that are annotated.
 *
 * @param <T> POJO type
 *
 * @author Guillaume CHAUVET
 */
public class MojetLineMapper<T> extends AbstractMojetLine<T> implements LineMapper<T> {

    private final DefaultLineMapper<T> delegate = new DefaultLineMapper<>();

    /**
     * Construct a new pojo {@link LineMapper} instance
     * 
     * @param targetType the bean type to manage
     */
    public MojetLineMapper(final Class<T> targetType) {
        super(targetType);
        final FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        final Map<String, Range> mapping = mapToRanges();
        final BeanWrapperFieldSetMapper<T> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(targetType);
        mapper.setDistanceLimit(0);
        tokenizer.setNames(mapping.keySet().toArray(new String[0]));
        tokenizer.setColumns(mapping.values().toArray(new Range[0]));
        tokenizer.setStrict(false);
        delegate.setLineTokenizer(tokenizer);
        delegate.setFieldSetMapper(mapper);
    }

    private Map<String, Range> mapToRanges() {
        final Map<String, Range> result = new LinkedHashMap<>();
        int current = 1;
        for (Entry<String, Field> field : mappedFields.entrySet()) {
            current = processFillers(field, current);
            current = processFragments(field, result, current) + 1;
        }
        return result;
    }

    private int processFillers(final Entry<String, Field> field, int current) {
        final Filler[] fillers = field.getValue().getAnnotationsByType(Filler.class);
        if (null != fillers && fillers.length > 0) {
            for (Filler filler : fillers) {
                if (filler.length() <= 1) {
                    throw new MojetRuntimeException("Natural number expected on filler " + field.getKey());
                }
                current += filler.length();
            }
            --current;
        }
        return current;
    }

    private int processFragments(final Entry<String, Field> field, final Map<String, Range> result, int current) {
        final Fragment fragment = field.getValue().getAnnotation(Fragment.class);
        if (null != fragment) {
            final int length = fragment.length();
            if (length <= 1) {
                throw new MojetRuntimeException("Natural number expected on fragment " + field.getKey());
            }
            final int end = current + length - 1;
            result.put(field.getKey(), new Range(current, end));
            current = end;
        }
        return current;
    }

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        return delegate.mapLine(line, lineNumber);
    }
}
