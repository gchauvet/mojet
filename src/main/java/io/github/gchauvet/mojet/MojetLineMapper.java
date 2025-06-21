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

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.validation.DataBinder;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MojetLineMapper is an implementation of LineMapper that uses a POJO type to map data that are annotated.
 * @param <T> POJO type
 * 
 * @author Guillaume CHAUVET
 */
public class MojetLineMapper<T> implements LineMapper<T> {
  private final DefaultLineMapper<T> delegate = new DefaultLineMapper<>();

  public MojetLineMapper(Class<T> targetType) {
    final FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    final Map<String, Range> mapping = mapAnnotatedFields(targetType, "", 1);
    tokenizer.setNames(mapping.keySet().toArray(new String[0]));
    tokenizer.setColumns(mapping.values().toArray(new Range[0]));
    delegate.setLineTokenizer(tokenizer);
    delegate.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
      {
        setTargetType(targetType);
        setDistanceLimit(0);
      }
      @Override
      protected void initBinder(DataBinder binder) {
        super.initBinder(binder);
      }
    });
  }

  /**
   * Browse the class fields and create a map of fields annotated with @Fragment.
   * If a field is a custom class annotated with @Registration, it is explored recursively.
   *
   * @param clazz la classe to wrap
   * @param prefix prefix for field names
   * @param index starting index for field mapping
   * @return a map of field names and their corresponding ranges
   */
  private Map<String, Range> mapAnnotatedFields(Class<?> clazz, String prefix, int index) {
    final Map<String, Range> fieldMap = new LinkedHashMap<>();

    for (Field field : clazz.getDeclaredFields()) {
      final Padding padding = field.getAnnotation(Padding.class);
      if (padding != null) {
          for (Filler filler : padding.value()) {
              index += filler.length();
          }
      }
      final Fragment champ = field.getAnnotation(Fragment.class);
      if (champ != null) {
        int end = index + champ.length() - 1;
        fieldMap.put(prefix + field.getName(), new Range(index, end));
        index = end;
      } else if (field.getType().isAnnotationPresent(Record.class)) {
        // If the field is a custom class, we recursively explore
        for (Map.Entry<String, Range> entry : mapAnnotatedFields(field.getType(), prefix + field.getName() + ".", index).entrySet()) {
          Range range = entry.getValue();
          fieldMap.put(entry.getKey(), range);
          index = range.getMax();
        }
      }
      index++;
    }
    return fieldMap;
  }

  @Override
  public T mapLine(String line, int lineNumber) throws Exception {
    return delegate.mapLine(line, lineNumber);
  }
}
